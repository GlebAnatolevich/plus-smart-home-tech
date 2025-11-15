package ru.practicum.kafka.telemetry.model.sensor.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.SensorEventType;

/**
 * Событие датчика освещенности, содержащее информацию о качестве связи и уровне освещенности.
 */
@Setter
@Getter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LightSensorEvent extends SensorEvent {
    Integer linkQuality;
    Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}