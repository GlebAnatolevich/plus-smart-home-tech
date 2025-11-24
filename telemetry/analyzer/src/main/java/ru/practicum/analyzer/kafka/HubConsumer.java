package ru.practicum.analyzer.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.mapper.AvroToEntityMapper;
import ru.practicum.analyzer.model.Sensor;
import ru.practicum.analyzer.repository.SensorRepository;
import ru.practicum.analyzer.service.ScenarioService;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class HubConsumer {
    SensorRepository sensorRepository;
    ScenarioService scenarioService;
    AvroToEntityMapper mapper;

    @KafkaListener(
            topics = "${spring.kafka.hub.consumer-topics}",
            containerFactory = "hubKafkaListenerContainerFactory"
    )
    public void listenHub(HubEventAvro hubEventAvro, Acknowledgment acknowledgment) {
        try {
            if (hubEventAvro == null) {
                log.warn("Получено null-событие");
                acknowledgment.acknowledge();
                return;
            }

            String hubId = hubEventAvro.getHubId();
            if (hubId == null) {
                log.warn("hubId == null, пропускаем сообщение");
                acknowledgment.acknowledge();
                return;
            }

            Object payload = hubEventAvro.getPayload();
            if (payload == null) {
                log.warn("hubId={}: payload == null, пропускаем", hubId);
                acknowledgment.acknowledge();
                return;
            }

            log.info("Получено событие хаба: hubId = {}, тип = {}",
                    hubId, payload.getClass().getSimpleName());

            switch (payload) {
                case DeviceAddedEventAvro added -> {
                    String deviceId = added.getId();
                    if (!sensorRepository.existsById(deviceId)) {
                        Sensor sensor = mapper.toSensor(hubId, added);
                        sensorRepository.save(sensor);
                        log.info("Датчик добавлен: id = {}, hubId = {}", deviceId, hubId);
                    } else {
                        log.info("Датчик уже существует: id = {}, hubId = {}, пропускаем", deviceId, hubId);
                    }
                }
                case DeviceRemovedEventAvro removed -> {
                    sensorRepository.deleteById(removed.getId());
                    log.info("Датчик удалён: id = {}, hubId = {}", removed.getId(), hubId);
                }
                case ScenarioAddedEventAvro scenarioAdded -> {
                    String scenarioName = scenarioAdded.getName();
                    scenarioService.saveOrUpdateScenario(hubEventAvro);
                    log.info("Сценарий сохранён/обновлён: имя = {}, hubId = {}", scenarioName, hubId);
                }
                case ScenarioRemovedEventAvro scenarioRemoved -> {
                    String scenarioName = scenarioRemoved.getName();
                    scenarioService.removeScenario(hubId, scenarioName);
                    log.info("Сценарий удалён: имя = {}, hubId = {}", scenarioName, hubId);
                }
                default -> log.warn("Необработанный тип события: {}", payload.getClass().getSimpleName());
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Ошибка при обработке события: hubId={}, тип={}",
                    hubEventAvro != null ? hubEventAvro.getHubId() : "unknown",
                    hubEventAvro != null && hubEventAvro.getPayload() != null
                            ? hubEventAvro.getPayload().getClass().getSimpleName()
                            : "unknown payload",
                    e);
        }
    }
}
