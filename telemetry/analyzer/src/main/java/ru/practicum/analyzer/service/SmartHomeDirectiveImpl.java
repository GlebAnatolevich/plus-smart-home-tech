package ru.practicum.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.client.HubRouterClient;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.ScenarioAction;
import ru.practicum.analyzer.model.enums.ConditionOperation;
import ru.practicum.analyzer.model.enums.ConditionType;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class SmartHomeDirectiveImpl implements SmartHomeDirective {
    ScenarioRepository scenarioRepository;
    HubRouterClient hubRouterClient;

    @Override
    @Transactional(readOnly = true)
    public void update(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        Map<String, SensorStateAvro> stateMap = snapshot.getSensorsState();

        log.info("Получен снимок состояния от hubId: {}", hubId);

        List<Scenario> scenarios = scenarioRepository.findAllByHubId(hubId);
        log.info("Найдено сценариев для hubId: {} = {}", hubId, scenarios.size());

        for (Scenario scenario : scenarios) {
            log.debug("Проверяем сценарий: {}", scenario.getName());

            boolean allConditionsOk = scenario.getConditions().stream()
                    .allMatch(sc -> {
                        String sensorId = sc.getSensor().getId();
                        SensorStateAvro sensorState = stateMap.get(sensorId);
                        if (sensorState == null) {
                            log.warn("Нет состояния для датчика: {}, пропускаем сценарий", sensorId);
                            return false;
                        }
                        int actual = extractValue(sensorState, sc.getCondition());
                        Condition cond = sc.getCondition();
                        boolean ok = evaluate(cond.getOperation(), actual, cond.getValue());
                        log.debug("датчик={} тип={} операция={} порог={} текущее={} --- {}",
                                sensorId,
                                cond.getType(),
                                cond.getOperation(),
                                cond.getValue(),
                                actual,
                                ok ? "OK" : "FAIL"
                        );
                        return ok;
                    });

            if (allConditionsOk) {
                int actionsCount = scenario.getActions().size();
                log.debug("Все условия выполнены, выполняем {} действий", actionsCount);
                for (ScenarioAction action : scenario.getActions()) {
                    hubRouterClient.sendDeviceAction(
                            hubId,
                            scenario.getName(),
                            action
                    );
                }
            } else {
                log.info("Условия не выполнены, действий по сценарию: {} не будет", scenario);
            }
        }
    }

    private int extractValue(SensorStateAvro state, Condition condition) {
        Object data = state.getData();
        ConditionType type = condition.getType();

        return switch (data) {
            case ClimateSensorAvro c -> switch (type) {
                case TEMPERATURE -> c.getTemperatureC();
                case HUMIDITY -> c.getHumidity();
                case CO2LEVEL -> c.getCo2Level();
                default -> {
                    log.warn("Тип условия: {} неприменим к ClimateSensorAvro", type);
                    yield 0;
                }
            };
            case TemperatureSensorAvro t -> switch (type) {
                case TEMPERATURE -> t.getTemperatureC();
                default -> {
                    log.warn("Тип условия: {} неприменим к TemperatureSensorAvro", type);
                    yield 0;
                }
            };
            case LightSensorAvro l -> switch (type) {
                case LUMINOSITY -> l.getLuminosity();
                default -> {
                    log.warn("Тип условия: {} неприменим к LightSensorAvro", type);
                    yield 0;
                }
            };
            case MotionSensorAvro m -> switch (type) {
                case MOTION -> m.getMotion() ? 1 : 0;
                default -> {
                    log.warn("Тип условия: {} неприменим к MotionSensorAvro", type);
                    yield 0;
                }
            };
            case SwitchSensorAvro s -> switch (type) {
                case SWITCH -> s.getState() ? 1 : 0;
                default -> {
                    log.warn("Тип условия: {} неприменим к SwitchSensorAvro", type);
                    yield 0;
                }
            };
            default -> {
                String cls = data.getClass().getSimpleName();
                throw new IllegalArgumentException("Неизвестный тип данных датчика: " + cls);
            }
        };
    }

    private boolean evaluate(ConditionOperation op, int actual, int target) {
        return switch (op) {
            case EQUALS -> actual == target;
            case GREATER_THAN -> actual > target;
            case LOWER_THAN -> actual < target;
        };
    }
}
