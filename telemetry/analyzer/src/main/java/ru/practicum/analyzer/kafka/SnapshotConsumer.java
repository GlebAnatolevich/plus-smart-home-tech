package ru.practicum.analyzer.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.SmartHomeDirective;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class SnapshotConsumer {
    SmartHomeDirective smartHomeDirective;

    @KafkaListener(
            containerFactory = "snapshotKafkaListenerContainerFactory",
            topics = "${spring.kafka.snapshot.consumer-topics}"
    )
    public void listenSnapshot(SensorsSnapshotAvro snapshotAvro, Acknowledgment acknowledgment) {
        String hubId = snapshotAvro != null ? snapshotAvro.getHubId() : "unknown";
        try {
            if (snapshotAvro == null) {
                log.warn("Получен null снимок, пропускаем");
                acknowledgment.acknowledge();
                return;
            }

            log.info("Получен снимок для hubId: {}", hubId);
            smartHomeDirective.update(snapshotAvro);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка при обработке снимка для hubId: {}, ошибка: {}", hubId, e.getMessage(), e);
        }
    }
}
