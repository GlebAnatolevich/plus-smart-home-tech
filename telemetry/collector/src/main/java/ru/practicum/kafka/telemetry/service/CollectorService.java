package ru.practicum.kafka.telemetry.service;

import ru.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.practicum.kafka.telemetry.model.sensor.SensorEvent;

public interface CollectorService {
    void sendSensorEvent(SensorEvent sensorEvent);

    void sendHubEvent(HubEvent hubEvent);
}
