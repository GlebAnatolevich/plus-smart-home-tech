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
 * Событие климатического датчика, содержащее информацию о температуре, влажности и уровне углекислого газа
 */
@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClimateSensorEvent extends SensorEvent {
    @NotNull
    Integer temperatureC;

    @NotNull
    Integer humidity;

    @NotNull
    Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
