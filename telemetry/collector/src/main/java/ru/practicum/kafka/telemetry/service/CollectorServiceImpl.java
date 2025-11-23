package ru.practicum.kafka.telemetry.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.kafka.telemetry.config.TelemetryKafkaProducerConfig;
import ru.practicum.kafka.telemetry.mapper.EventMapper;
import ru.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.practicum.kafka.telemetry.model.hub.impl.DeviceAddedEvent;
import ru.practicum.kafka.telemetry.model.hub.impl.DeviceRemovedEvent;
import ru.practicum.kafka.telemetry.model.hub.impl.ScenarioAddedEvent;
import ru.practicum.kafka.telemetry.model.hub.impl.ScenarioRemovedEvent;
import ru.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.ClimateSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.LightSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.MotionSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.SwitchSensorEvent;
import ru.practicum.kafka.telemetry.model.sensor.impl.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CollectorServiceImpl implements CollectorService {
    KafkaTemplate<String, Object> kafkaTemplate;
    EventMapper eventMapper;
    TelemetryKafkaProducerConfig config;

    @Override
    public void sendSensorEvent(SensorEvent sensorEvent) {
        String sensorTopic = config.getSensorTopic();

        SensorEventAvro avroSensorEvent = switch (sensorEvent) {
            case ClimateSensorEvent climateEvent -> eventMapper.toAvro(climateEvent);
            case LightSensorEvent lightEvent -> eventMapper.toAvro(lightEvent);
            case MotionSensorEvent motionEvent -> eventMapper.toAvro(motionEvent);
            case SwitchSensorEvent switchEvent -> eventMapper.toAvro(switchEvent);
            case TemperatureSensorEvent temperatureEvent -> eventMapper.toAvro(temperatureEvent);
            default -> throw new IllegalArgumentException("Неподдерживаемый тип события датчика: "
                                                          + sensorEvent.getType());
        };

        log.info("В топик: {} отправляется событие датчика: {}", sensorTopic, avroSensorEvent);

        long eventTimestamp = avroSensorEvent.getTimestamp().toEpochMilli();

        kafkaTemplate.send(sensorTopic, null, eventTimestamp, avroSensorEvent.getHubId(), avroSensorEvent)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие датчика: {} для hubId: {} успешно отправлено, смещение: {}",
                                sensorEvent, avroSensorEvent.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Не удалось отправить событие датчика: {} для hubId: {}: {}",
                                sensorEvent, avroSensorEvent.getHubId(), exception.getMessage());
                    }
                });
    }

    @Override
    public void sendHubEvent(HubEvent hubEvent) {
        String hubTopic = config.getHubTopic();

        HubEventAvro avroHubEvent = switch (hubEvent) {
            case DeviceAddedEvent deviceAddedEvent -> eventMapper.toAvro(deviceAddedEvent);
            case DeviceRemovedEvent deviceRemovedEvent -> eventMapper.toAvro(deviceRemovedEvent);
            case ScenarioAddedEvent scenarioAddedEvent -> eventMapper.toAvro(scenarioAddedEvent);
            case ScenarioRemovedEvent scenarioRemovedEvent -> eventMapper.toAvro(scenarioRemovedEvent);
            default -> throw new IllegalArgumentException("Неподдерживаемый тип события хаба: " + hubEvent.getType());
        };

        log.info("В топик: {} отправляется событие хаба: {}", hubTopic, avroHubEvent);

        long eventTimestamp = avroHubEvent.getTimestamp().toEpochMilli();

        kafkaTemplate.send(hubTopic, null, eventTimestamp, avroHubEvent.getHubId(), avroHubEvent)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Событие хаба: {} для hubId: {} успешно отправлено, смещение: {}",
                                hubEvent, avroHubEvent.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Не удалось отправить событие хаба: {} для hubId: {}: {}",
                                hubEvent, avroHubEvent.getHubId(), exception.getMessage());
                    }
                });
    }
}
