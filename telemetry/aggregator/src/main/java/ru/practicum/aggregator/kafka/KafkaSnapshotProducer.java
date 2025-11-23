package ru.practicum.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaSnapshotProducer {
    private final KafkaTemplate<String, SensorsSnapshotAvro> kafkaTemplate;

    @Value("${aggregator.kafka.topic.snapshots}")
    private String snapshotsTopic;

    public void send(SensorsSnapshotAvro avroSnapshot) {

        log.info("В топик: {} отправляется снимок событий: {} для хаба: {}", snapshotsTopic, avroSnapshot,
                avroSnapshot.getHubId());

        kafkaTemplate.send(snapshotsTopic,null,
                        avroSnapshot.getTimestamp().toEpochMilli(), avroSnapshot.getHubId(), avroSnapshot)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие сенсора: {} для hubId: {} успешно отправлено, смещение: {}",
                                avroSnapshot, avroSnapshot.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Не удалось отправить событие сенсора: {} для hubId: {}: {}",
                                avroSnapshot, avroSnapshot.getHubId(), exception.getMessage());
                    }
                });
    }
}
