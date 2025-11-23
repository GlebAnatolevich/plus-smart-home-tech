package ru.practicum.kafka.telemetry.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.kafka.telemetry.handler.hub.GrpcHubEventHandler;
import ru.practicum.kafka.telemetry.handler.sensor.GrpcSensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@SuppressWarnings("unused")
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {
    Map<SensorEventProto.PayloadCase, GrpcSensorEventHandler> sensorHandlers;
    Map<HubEventProto.PayloadCase, GrpcHubEventHandler> hubHandlers;

    public CollectorController(Set<GrpcSensorEventHandler> sensorHandlers,
                               Set<GrpcHubEventHandler> hubHandlers) {
        this.sensorHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(GrpcSensorEventHandler::getMessageType, Function.identity()));
        this.hubHandlers = hubHandlers.stream()
                .collect(Collectors.toMap(GrpcHubEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            GrpcSensorEventHandler handler = sensorHandlers.get(request.getPayloadCase());
            if (handler == null) throw new IllegalArgumentException("Неизвестный тип датчика");
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMessage())
                    .withCause(e)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            GrpcHubEventHandler handler = hubHandlers.get(request.getPayloadCase());
            if (handler == null) throw new IllegalArgumentException("Неизвестный тип хаба");
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMessage())
                    .withCause(e)));
        }
    }
}
