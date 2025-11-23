package ru.practicum.kafka.telemetry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.practicum.kafka.telemetry.service.CollectorService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class CollectorController {
    private final CollectorService collectorService;

    @PostMapping("/sensors")
    public void sendSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Получено событие от датчика: {}", sensorEvent);
        collectorService.sendSensorEvent(sensorEvent);
    }

    @PostMapping("/hubs")
    public void sendHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Получено событие от хаба: {}", hubEvent);
        collectorService.sendHubEvent(hubEvent);
    }
}
