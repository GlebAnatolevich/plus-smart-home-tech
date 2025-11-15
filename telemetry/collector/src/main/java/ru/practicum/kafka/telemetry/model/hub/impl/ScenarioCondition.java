package ru.practicum.kafka.telemetry.model.hub.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.telemetry.model.hub.enums.ConditionOperation;
import ru.practicum.kafka.telemetry.model.hub.enums.ConditionType;

/**
 * Условие сценария, которое содержит информацию о датчике, типе условия, операции и значении.
 */
@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {
    String sensorId;
    ConditionType type;
    ConditionOperation operation;
    Integer value;
}
