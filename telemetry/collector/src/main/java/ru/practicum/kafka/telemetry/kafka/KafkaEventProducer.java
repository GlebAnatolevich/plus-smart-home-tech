package ru.practicum.kafka.telemetry.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.telemetry.config.TelemetryKafkaProducerConfig;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaEventProducer {
    KafkaTemplate<String, Object> kafkaTemplate;
    TelemetryKafkaProducerConfig config;

    public void sendHubEvent(HubEventAvro avroHubEvent) {
        String hubTopic = config.getHubTopic();

        log.info("В топик: {} отправляется событие хаба: {}", hubTopic, avroHubEvent);

        long eventTimestamp = avroHubEvent.getTimestamp().toEpochMilli();

        kafkaTemplate.send(hubTopic, null, eventTimestamp, avroHubEvent.getHubId(), avroHubEvent)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие хаба: {} для hubId: {} успешно отправлено, смещение: {}",
                                avroHubEvent, avroHubEvent.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Не удалось отправить событие хаба: {} для hubId: {}: {}",
                                avroHubEvent, avroHubEvent.getHubId(), exception.getMessage());
                    }
                });
    }

    public void sendSensorEvent(SensorEventAvro avroSensorEvent) {
        String sensorTopic = config.getSensorTopic();

        log.info("В топик: {} отправляется событие датчика: {}", sensorTopic, avroSensorEvent);

        long eventTimestamp = avroSensorEvent.getTimestamp().toEpochMilli();

        kafkaTemplate.send(sensorTopic, null, eventTimestamp, avroSensorEvent.getHubId(), avroSensorEvent)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие датчика: {} для hubId: {} успешно отправлено, смещение: {}",
                                avroSensorEvent, avroSensorEvent.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Не удалось отправить событие датчика: {} для hubId: {}: {}",
                                avroSensorEvent, avroSensorEvent.getHubId(), exception.getMessage());
                    }
                });
    }
}
