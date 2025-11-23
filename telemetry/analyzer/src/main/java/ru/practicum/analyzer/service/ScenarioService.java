package ru.practicum.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.mapper.AvroToEntityMapper;
import ru.practicum.analyzer.model.*;
import ru.practicum.analyzer.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScenarioService {
    ScenarioRepository scenarioRepository;
    SensorRepository sensorRepository;
    ConditionRepository conditionRepository;
    ActionRepository actionRepository;
    ScenarioConditionRepository scenarioConditionRepo;
    ScenarioActionRepository scenarioActionRepo;
    AvroToEntityMapper mapper;

    @Transactional
    public void saveOrUpdateScenario(HubEventAvro hubEvent) {
        if (hubEvent == null || hubEvent.getPayload() == null) {
            log.warn("Неверное событие: hubEvent или payload == null");
            return;
        }

        String hubId = hubEvent.getHubId();
        ScenarioAddedEventAvro evt = (ScenarioAddedEventAvro) hubEvent.getPayload();

        log.info("ScenarioAddedEventAvro: {}", evt);

        // --- Сохраняем или находим сам сценарий
        Scenario scenario = scenarioRepository
                .findByHubIdAndName(hubId, evt.getName())
                .orElseGet(() -> mapper.toScenario(hubId, evt));
        scenario.setHubId(hubId);
        scenario.setName(evt.getName());
        scenario = scenarioRepository.save(scenario);

        log.info("Сохранён сценарий: {}", scenario);

        // --- Удаляем старые связи
        Long scenarioId = scenario.getId();
        scenarioConditionRepo.deleteByIdScenarioId(scenarioId);
        scenarioActionRepo.deleteByIdScenarioId(scenarioId);

        // --- Сохраняем условия
        if (evt.getConditions() != null) {
            for (ScenarioConditionAvro avro : evt.getConditions()) {
                String sensorId = avro.getSensorId();

                // --- если датчик не найден, то создаём его
                Sensor sensor = sensorRepository.findById(sensorId)
                        .orElseGet(() -> {
                            Sensor s = Sensor.builder()
                                    .id(sensorId)
                                    .hubId(hubId)
                                    .build();
                            log.info("Автосоздаём датчик: {} для хаба: {}", sensorId, hubId);
                            return sensorRepository.save(s);
                        });

                Condition condition = mapper.toCondition(avro);
                condition = conditionRepository.save(condition);

                ScenarioCondition sc = ScenarioCondition.builder()
                        .id(new ScenarioConditionId(scenarioId, sensorId, condition.getId()))
                        .scenario(scenario)
                        .sensor(sensor)
                        .condition(condition)
                        .build();
                scenarioConditionRepo.save(sc);
            }
        }

        // --- Сораняем действия
        if (evt.getActions() != null) {
            for (DeviceActionAvro avro : evt.getActions()) {
                String sensorId = avro.getSensorId();

                // --- если датчик не найден, то создаём его
                Sensor sensor = sensorRepository.findById(sensorId)
                        .orElseGet(() -> {
                            Sensor s = Sensor.builder()
                                    .id(sensorId)
                                    .hubId(hubId)
                                    .build();
                            log.info("Автосоздаём датчик: {} для хаба: {}", sensorId, hubId);
                            return sensorRepository.save(s);
                        });

                Action action = mapper.toAction(avro);
                action = actionRepository.save(action);

                ScenarioAction sa = ScenarioAction.builder()
                        .id(new ScenarioActionId(scenarioId, sensorId, action.getId()))
                        .scenario(scenario)
                        .sensor(sensor)
                        .action(action)
                        .build();
                scenarioActionRepo.save(sa);
            }
        }

        log.info("Сценарий: {} для хаба: {} сохранён/обновлён: условия = {}, действия = {}",
                scenario.getName(), hubId,
                evt.getConditions() != null ? evt.getConditions().size() : 0,
                evt.getActions() != null ? evt.getActions().size() : 0);
    }

    @Transactional
    public void removeScenario(String hubId, String name) {
        scenarioRepository.findByHubIdAndName(hubId, name).ifPresent(s -> {
            scenarioConditionRepo.deleteByIdScenarioId(s.getId());
            scenarioActionRepo.deleteByIdScenarioId(s.getId());
            scenarioRepository.delete(s);
            log.info("Сценарий: {} для хаба: {} удалён.", name, hubId);
        });
    }
}
