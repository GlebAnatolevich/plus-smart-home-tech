package ru.practicum.aggregator.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Properties;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Getter
@SuppressWarnings("unused")
public class AggregatorKafkaConsumerConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    String bootstrapServers;

    @Value("${spring.kafka.consumer.key-deserializer}")
    String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    String valueDeserializer;

    @Value("${aggregator.kafka.topic.sensors}")
    String sensorTopic;

    @Value("${spring.kafka.consumer.group-id}")
    String clientGroupId;

    @Value("${spring.kafka.consumer.properties.max.poll.records}")
    int maxPollRecords;

    @Value("${spring.kafka.consumer.properties.fetch.min.bytes}")
    int fetchMinBytes;

    @Value("${spring.kafka.consumer.properties.enable.auto.commit}")
    boolean enableAutoCommit;

    @Value("${spring.kafka.consumer.properties.fetch.max.wait.ms}")
    int fetchMaxWaitMs;

    @Value("${spring.kafka.consumer.properties.max.partition.fetch.bytes}")
    int maxPartitionFetchBytes;

    @Bean
    public KafkaConsumer<String, SensorEventAvro> kafkaConsumer() {
        Properties properties = getConsumerProperties();
        return new KafkaConsumer<>(properties);
    }

    private Properties getConsumerProperties() {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, clientGroupId);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);

        return properties;
    }
}
