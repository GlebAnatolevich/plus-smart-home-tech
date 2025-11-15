package ru.practicum.kafka.telemetry.model.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.telemetry.model.sensor.impl.ClimateSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.LightSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.MotionSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.SwitchSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.TemperatureSensorEvent;

import java.time.Instant;

@Setter
@Getter
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = SensorEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class SensorEvent {
    @NotBlank
    String id;

    @NotBlank
    String hubId;

    Instant timestamp = Instant.now();

    public abstract SensorEventType getType();
}
