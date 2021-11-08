package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfiguration {

    @Bean
    RestTemplate wildberriesSupplierAndOptionsClient() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("https://wbx-content-v2.wbstatic.net"));
        return restTemplate;
    }

    @Bean
    ExecutorService wildberriesExecutorService() {
        return Executors.newFixedThreadPool(20);
    }
}
