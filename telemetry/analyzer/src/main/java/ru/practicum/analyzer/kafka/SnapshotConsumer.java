package ru.practicum.analyzer.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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
    public void listenSnapshot(SensorsSnapshotAvro snapshotAvro) {
        String hubId = snapshotAvro.getHubId();
        try {
            log.info("Получен снимок для hubId: {}", hubId);
            smartHomeDirective.update(snapshotAvro);
        } catch (Exception e) {
            log.error("Ошибка при обработке снимка для hubId: {}, ошибка: {}", hubId, e.getMessage(), e);
        }
    }
}
