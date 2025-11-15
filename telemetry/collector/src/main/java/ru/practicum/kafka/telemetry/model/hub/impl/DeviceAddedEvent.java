package ru.practicum.kafka.telemetry.model.hub.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.practicum.kafka.telemetry.model.hub.enums.DeviceType;
import ru.practicum.kafka.telemetry.model.hub.enums.HubEventType;

/**
 * Событие, сигнализирующее о добавлении нового устройства в систему.
 */
@Setter
@Getter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    String id;

    @NotNull
    DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
