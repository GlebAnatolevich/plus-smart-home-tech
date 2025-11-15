package ru.practicum.kafka.telemetry.model.sensor.impl;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.SensorEventType;

/**
 * Событие датчика переключателя, содержащее информацию о текущем состоянии переключателя.
 */
@Setter
@Getter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SwitchSensorEvent extends SensorEvent {
    @NotNull
    Boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}