package com.example.demo;

import com.example.demo.service.WildberriesService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Resource
    WildberriesService wildberriesService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        List<Integer> ids = Arrays.asList(12403166, 2147837, 15556062, 40019873);
        System.out.println(wildberriesService.getWildberriesData(ids));
        System.exit(0);
    }
}
