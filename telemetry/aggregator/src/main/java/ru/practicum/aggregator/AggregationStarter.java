package ru.practicum.aggregator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.config.AggregatorKafkaConsumerConfig;
import ru.practicum.aggregator.kafka.KafkaSnapshotProducer;
import ru.practicum.aggregator.service.SensorSnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationStarter {
    KafkaConsumer<String, SensorEventAvro> consumer;
    KafkaSnapshotProducer producer;
    SensorSnapshotService snapshotService;
    AggregatorKafkaConsumerConfig config;
    Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();

    public void start() {
        log.info("Kafka Consumer успешно внедрён");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Сигнал завершения получен, будим Consumer из другого потока для генерации методом poll " +
                    "исключения WakeupException");
            consumer.wakeup();
        }));

        try {
            String topic = config.getSensorTopic();
            consumer.subscribe(Collections.singletonList(topic));
            log.info("Подписка на топик: {}", topic);

            while (true) {
                log.debug("Ожидание новых сообщений");
                ConsumerRecords<String, SensorEventAvro> records =
                        consumer.poll(Duration.ofMillis(config.getFetchMaxWaitMs()));
                handleMessages(records);
            }

        } catch (WakeupException ignored) {
            log.info("Consumer пробуждён, выходим из цикла");
        } catch (Exception e) {
            log.error("Неизвестное исключение в цикле Consumer", e);
        } finally {
            commitOffsets();
        }
    }

    private void handleMessages(ConsumerRecords<String, SensorEventAvro> records) {
        for (ConsumerRecord<String, SensorEventAvro> record : records) {
            log.debug("Получена запись: partition={}, offset={}, value={}", record.partition(), record.offset(),
                    record.value());
            SensorEventAvro avroSensorEvent = record.value();

            snapshotService.updateSnapshot(avroSensorEvent).ifPresent(snapshot -> {
                producer.send(snapshot);
                currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1));
            });
        }

        if (!currentOffsets.isEmpty()) {
            consumer.commitAsync(currentOffsets, (offsets, ex) -> {
                if (ex != null) {
                    log.error("Ошибка при коммите оффсетов {}", offsets, ex);
                }
            });
        }
    }

    private void commitOffsets() {
        try {
            log.info("Коммитим финальные оффсеты");
            consumer.commitSync(currentOffsets);
        } catch (Exception e) {
            log.error("Ошибка при коммите финальных оффсетов", e);
        } finally {
            log.info("Закрываем Consumer");
            consumer.close();
        }
    }
}
