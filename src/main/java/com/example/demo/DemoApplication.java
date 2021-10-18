package com.example.demo;

import com.example.demo.json.WildberriesCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private List<WildberriesCategory> categoryList = new ArrayList<>();

    @Resource
    RestTemplate wildberriesClient;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String response = wildberriesClient.getForObject("/gettopmenuinner?lang=ru", String.class);
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonValue = (JSONObject) jsonObject.get("value");
        JSONArray jsonMenu = (JSONArray) jsonValue.get("menu");
        getCategoryRow(jsonMenu, null);
        for (WildberriesCategory category : this.categoryList) {
            System.out.println(category);
        }
        System.exit(0);
    }

    private void getCategoryRow(JSONArray menu, Long parentId) {
        for (int i = 0; i < menu.length(); i++) {
            JSONObject item = (JSONObject) menu.get(i);
            Long id = item.getLong("id");
            WildberriesCategory category = new WildberriesCategory(id, parentId, item.getString("name"));
            this.categoryList.add(category);
            JSONArray childMenu;
            try {
                childMenu = item.getJSONArray("childs");
            } catch (Exception e) {
                childMenu = null;
            }
            if (childMenu != null) {
                getCategoryRow(childMenu, id); // чтобы понять рекурсию - надо понять рекурсию :-)
            }
        }
    }
}
