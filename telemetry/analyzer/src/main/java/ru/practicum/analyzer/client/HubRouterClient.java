package ru.practicum.analyzer.client;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class HubRouterClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub stub;

    public void sendDeviceAction(String hubId, String scenarioName, ScenarioAction action) {
        try {
            ActionTypeProto typeProto = ActionTypeProto.valueOf(action.getAction().getType().name());

            DeviceActionProto.Builder actionB = DeviceActionProto.newBuilder()
                    .setSensorId(action.getId().getSensorId())
                    .setType(typeProto);

            Integer val = action.getAction().getValue();
            if (val != null) {
                actionB.setValue(val);
            }

            DeviceActionProto actionProto = actionB.build();

            DeviceActionRequestProto request = DeviceActionRequestProto.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenarioName)
                    .setAction(actionProto)
                    .setTimestamp(currentTimestamp())
                    .build();

            stub.handleDeviceAction(request);
            log.info("gRPC hub-router: hubId='{}', scenario='{}', action={}",
                    hubId, scenarioName, actionProto);

        } catch (Exception ex) {
            log.error("Ошибка отправки в hub-router: {}", ex.getMessage(), ex);
        }
    }

    private Timestamp currentTimestamp() {
        Instant now = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }
}
