package ru.practicum.analyzer.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Configuration
@Slf4j
@EnableConfigurationProperties(GrpcClientProperties.class)
@RequiredArgsConstructor
public class GrpcClientConfig {
    private final GrpcClientProperties props;

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel hubRouterChannel() {
        boolean plaintext = "plaintext".equalsIgnoreCase(props.getNegotiationType());

        ManagedChannelBuilder<?> builder = ManagedChannelBuilder
                .forTarget(props.getAddress());

        if (plaintext) {
            builder.usePlaintext();
        }
        if (props.isEnableKeepAlive()) {
            builder.keepAliveWithoutCalls(props.isKeepAliveWithoutCalls());
        }

        ManagedChannel channel = builder.build();

        log.info(
                "gRPC hub-router channel init: address={}, plaintext={}, enableKeepAlive={}, keepAliveWithoutCalls={}",
                props.getAddress(),
                plaintext,
                props.isEnableKeepAlive(),
                props.isKeepAliveWithoutCalls()
        );

        return channel;
    }

    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub(
            ManagedChannel hubRouterChannel) {
        return HubRouterControllerGrpc.newBlockingStub(hubRouterChannel);
    }
}
