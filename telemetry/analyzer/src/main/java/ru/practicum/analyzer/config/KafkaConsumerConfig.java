package ru.practicum.analyzer.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConfigurationProperties("spring.kafka")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@SuppressWarnings("unused")
public class KafkaConsumerConfig {
    Hub hub = new Hub();
    Snapshot snapshot = new Snapshot();

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hub {
        String bootstrapServers;
        String groupId;
        String clientId;
        String consumerTopics;
        String valueDeserializer;
        String keyDeserializer;
        boolean enableAutoCommit;
        int autoCommitInterval;
        String autoOffsetReset;
        int maxPollRecords;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Snapshot {
        String bootstrapServers;
        String groupId;
        String clientId;
        String consumerTopics;
        String valueDeserializer;
        String keyDeserializer;
        boolean enableAutoCommit;
        String autoOffsetReset;
        int maxPollRecords;
        int maxPollInterval;
        String listenerAckMode;
        String isolationLevel;
    }

    @Bean
    public ConsumerFactory<String, HubEventAvro> hubConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hub.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, hub.getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, hub.getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hub.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hub.getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, hub.isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, hub.getAutoCommitInterval());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, hub.getAutoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, hub.getMaxPollRecords());

        log.debug("Создание hubConsumerFactory с конфигурацией: {}", props);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "hubKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> hubKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hubConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        log.debug("Создание hubKafkaListenerContainerFactory для чтения сообщений топиков: {}", hub.getConsumerTopics());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, snapshot.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, snapshot.getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshot.getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshot.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshot.getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, snapshot.isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, snapshot.getAutoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, snapshot.getMaxPollRecords());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, snapshot.getMaxPollInterval());
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, snapshot.getIsolationLevel());

        log.debug("Создание snapshotConsumerFactory с конфигурацией: {}", props);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "snapshotKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> snapshotKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snapshotConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        log.debug("Создание snapshotKafkaListenerContainerFactory для чтения сообщений топиков: {}", snapshot.getConsumerTopics());

        return factory;
    }
}
