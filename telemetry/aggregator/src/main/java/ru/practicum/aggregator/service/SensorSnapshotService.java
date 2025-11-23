package ru.practicum.aggregator.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensorSnapshotService {
    Map<String, SensorsSnapshotAvro> snapshotsByHubId = new ConcurrentHashMap<>();

    public Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro avroSensorEvent) {
        /**
         * Получаем текущий снимок датчиков для заданного идентификатора хаба
         * При отсутствии создаем
         */
        SensorsSnapshotAvro currentSnapshot = snapshotsByHubId.computeIfAbsent(avroSensorEvent.getHubId(), hubId ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setSensorsState(new HashMap<>())
                        .setTimestamp(avroSensorEvent.getTimestamp())
                        .build()
        );

        /**
         * Получаем предыдущее состояние датчика по его идентификатору
         */
        SensorStateAvro previousSensorState = currentSnapshot.getSensorsState().get(avroSensorEvent.getId());

        /**
         *
         * Если время создания старого события позже или событие осталось без изменения, возвращаем пустой Optional
         */
        if (previousSensorState != null && (
                previousSensorState.getTimestamp().isAfter(avroSensorEvent.getTimestamp()) ||
                previousSensorState.getData().equals(avroSensorEvent.getPayload())
        )) {
            return Optional.empty();
        }

        SensorStateAvro newSensorState = SensorStateAvro.newBuilder()
                .setTimestamp(avroSensorEvent.getTimestamp())
                .setData(avroSensorEvent.getPayload())
                .build();

        currentSnapshot.getSensorsState().put(avroSensorEvent.getId(), newSensorState);
        currentSnapshot.setTimestamp(avroSensorEvent.getTimestamp());

        return Optional.of(currentSnapshot);
    }
}
