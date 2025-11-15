package ru.practicum.kafka.telemetry.model.hub.impl;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.practicum.kafka.telemetry.model.hub.enums.HubEventType;

/**
 * Событие, сигнализирующее об удалении устройства из системы.
 */
@Setter
@Getter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
