package ru.practicum.kafka.telemetry.handler.sensor.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.telemetry.handler.sensor.GrpcSensorEventHandler;
import ru.practicum.kafka.telemetry.kafka.KafkaEventProducer;
import ru.practicum.kafka.telemetry.mapper.ProtobufToAvroMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class SwitchSensorGrpcHandler implements GrpcSensorEventHandler {
    ProtobufToAvroMapper mapper;
    KafkaEventProducer producer;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro avro = mapper.toAvro(event);
        producer.sendSensorEvent(avro);
    }
}
