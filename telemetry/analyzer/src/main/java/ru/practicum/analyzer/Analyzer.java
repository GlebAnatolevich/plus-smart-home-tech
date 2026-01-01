package ru.practicum.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.practicum.analyzer.config.KafkaConsumerConfig;

@SpringBootApplication
@EnableConfigurationProperties(KafkaConsumerConfig.class)
@ConfigurationPropertiesScan
public class Analyzer {
    public static void main(String[] args) {
        SpringApplication.run(Analyzer.class, args);
    }
}
