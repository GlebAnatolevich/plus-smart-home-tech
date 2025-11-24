package ru.practicum.kafka.telemetry.handler.hub.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.kafka.telemetry.handler.hub.GrpcHubEventHandler;
import ru.practicum.kafka.telemetry.kafka.KafkaEventProducer;
import ru.practicum.kafka.telemetry.mapper.ProtobufToAvroMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class ScenarioRemovedGrpcHandler implements GrpcHubEventHandler {
    ProtobufToAvroMapper mapper;
    KafkaEventProducer producer;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro avro = mapper.toAvro(event);
        producer.sendHubEvent(avro);
    }
}
