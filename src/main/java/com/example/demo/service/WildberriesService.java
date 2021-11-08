package com.example.demo.service;

import com.example.demo.utility.TaskUtility;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class WildberriesService {
    @Resource
    RestTemplate wildberriesClient;

    @Resource
    ExecutorService wildberriesExecutorService;

    private String getOptions(Integer wildberriesId) {
        String url = "https://wbx-content-v2.wbstatic.net/ru/" + wildberriesId + ".json?locale=ru";
        return wildberriesClient.getForObject(url, String.class);
    }

    private String getSupplier(Integer wildberriesId) {
        String url = "https://wbx-content-v2.wbstatic.net/sellers/" + wildberriesId + ".json?locale=ru";
        return wildberriesClient.getForObject(url, String.class);
    }

    private  Map<String, String> getSuppliersAndOptionRawData(Integer wildberriesId) {
        List<CompletableFuture<String>> gettingRawDataTasks = new ArrayList<>();
        gettingRawDataTasks.add(CompletableFuture.supplyAsync(() -> getOptions(wildberriesId)));
        gettingRawDataTasks.add(CompletableFuture.supplyAsync(() -> getSupplier(wildberriesId)));

        CompletableFuture<List<String>> resultListTask = TaskUtility.allToList(gettingRawDataTasks);
        List<String> rawDataListResult = resultListTask.join();


        Map<String, String> result = new HashMap<>();
        result.put("options", rawDataListResult.get(0));
        result.put("supplier", rawDataListResult.get(1));
        return result;
    }

    private List<Map<String, String>> getSuppliersAndOptionRawDataList(List<Integer> wildberriesIds) {
        List<CompletableFuture<Map<String, String>>> gettingRawDataTaskList = new ArrayList<>();
        for (Integer wildberriesId : wildberriesIds) {
            CompletableFuture<Map<String, String>> task = CompletableFuture
                    .supplyAsync(() -> getSuppliersAndOptionRawData(wildberriesId), this.wildberriesExecutorService);
            gettingRawDataTaskList.add(task);
        }

        CompletableFuture<List<Map<String, String>>> resultListTask = TaskUtility.allToList(gettingRawDataTaskList);
        List<Map<String, String>> resultList = resultListTask.join();
        return resultList;
    }

    private String getCatalogData(List<Integer> wildberiesIds) {
        List<String> stringIds = wildberiesIds.stream().map(id -> id.toString()).collect(Collectors.toList());
        String joinedIds = String.join(";", stringIds);
        String url = "https://wbxcatalog-ru.wildberries.ru/nm-2-card/catalog?locale=ru&nm=" + joinedIds;
        return wildberriesClient.getForObject(url, String.class);
    }

    public List<Map<String, String>> getWildberriesData(List<Integer> wildberiesIds) {
        List<Map<String, String>> result = new ArrayList<>();

        String catalogData = getCatalogData(wildberiesIds);
        List<Map<String, String>> supplierAndOptionsData = getSuppliersAndOptionRawDataList(wildberiesIds);

        JSONArray products = JsonPath.parse(catalogData).read("$.data.products[*]");
        for (int i = 0; i < products.size(); i++) {
            LinkedHashMap<String, String> product = (LinkedHashMap<String, String>) products.get(i);
            String productJsonString = new JSONObject(product).toString();
            Map<String, String> data = new HashMap<>();
            data.put("product", productJsonString);

            Map<String, String> supplierData = supplierAndOptionsData.
                    stream()
                    .filter(object -> object.get("supplier").contains(String.valueOf(product.get("id"))))
                    .findFirst().get();
            data.put("supplier", supplierData.get("supplier"));

            Map<String, String> optionsData = supplierAndOptionsData.
                    stream()
                    .filter(object -> object.get("options").contains(String.valueOf(product.get("id"))))
                    .findFirst().get();
            data.put("options", optionsData.get("options"));
            result.add(data);
        }

        return result;
    }
}
