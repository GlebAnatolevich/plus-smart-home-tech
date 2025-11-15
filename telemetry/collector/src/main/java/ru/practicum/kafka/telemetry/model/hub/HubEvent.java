package ru.practicum.kafka.telemetry.model.hub;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.telemetry.model.hub.enums.HubEventType;
import ru.practicum.kafka.telemetry.model.hub.impl.DeviceAddedEvent;
import ru.practicum.kafka.telemetry.model.hub.impl.DeviceRemovedEvent;
import ru.practicum.kafka.telemetry.model.hub.impl.ScenarioAddedEvent;
import ru.practicum.kafka.telemetry.model.hub.impl.ScenarioRemovedEvent;

import java.time.Instant;

@Setter
@Getter
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = HubEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED"),
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class HubEvent {
    @NotBlank
    String hubId;

    Instant timestamp = Instant.now();

    @NotNull
    public abstract HubEventType getType();
}
