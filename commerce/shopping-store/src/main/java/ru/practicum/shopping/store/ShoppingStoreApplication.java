package ru.practicum.shopping.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableDiscoveryClient
public class ShoppingStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingStoreApplication.class, args);
    }
}
