package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.Sensor;
import ru.practicum.analyzer.model.enums.ActionType;
import ru.practicum.analyzer.model.enums.ConditionOperation;
import ru.practicum.analyzer.model.enums.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
@SuppressWarnings("unused")
public interface AvroToEntityMapper {

    // --- Sensor
    @Mapping(target = "id",    source = "avro.id")
    @Mapping(target = "hubId", source = "hubId")
    Sensor toSensor(String hubId, DeviceAddedEventAvro avro);

    // --- Scenario (без вложенных списков, они будут собраны в сервисе)
    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "hubId",      source = "hubId")
    @Mapping(target = "name",       source = "avro.name")
    @Mapping(target = "conditions", ignore = true)
    @Mapping(target = "actions",    ignore = true)
    Scenario toScenario(String hubId, ScenarioAddedEventAvro avro);

    // --- Condition
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "type",      source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value",     source = "value", qualifiedByName = "unionToInteger")
    Condition toCondition(ScenarioConditionAvro avro);

    // --- преобразование списка в Set
    default Set<Condition> toConditionSet(List<ScenarioConditionAvro> avros) {
        if (avros == null) {
            return null;
        }
        Set<Condition> conditions = new HashSet<>();
        for (ScenarioConditionAvro avro : avros) {
            conditions.add(toCondition(avro));
        }
        return conditions;
    }


    // --- Action
    @Mapping(target = "id",    ignore = true)
    @Mapping(target = "type",  source = "type")
    @Mapping(target = "value", source = "value", qualifiedByName = "unionToInteger")
    Action toAction(DeviceActionAvro avro);

    // --- преобразование списка в Set
    default Set<Action> toActionSet(List<DeviceActionAvro> avros) {
        if (avros == null) {
            return null;
        }
        Set<Action> actions = new HashSet<>();
        for (DeviceActionAvro avro : avros) {
            actions.add(toAction(avro));
        }
        return actions;
    }

    // --- Преобразование union {null, int, boolean} в Integer
    @Named("unionToInteger")
    default Integer unionToInteger(Object value) {
        return switch (value) {
            case null -> null;
            case Integer i -> i;
            case Boolean b -> b ? 1 : 0;
            default -> Integer.parseInt(value.toString());
        };
    }

    // --- enum
    default ConditionType map(ConditionTypeAvro v) {
        return v == null ? null : ConditionType.valueOf(v.name());
    }

    default ConditionOperation map(ConditionOperationAvro v) {
        return v == null ? null : ConditionOperation.valueOf(v.name());
    }

    default ActionType map(ActionTypeAvro v) {
        return v == null ? null : ActionType.valueOf(v.name());
    }
}
