package ru.practicum.analyzer.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "grpc.client.hub-router")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GrpcClientProperties {
    String address;
    boolean enableKeepAlive;
    boolean keepAliveWithoutCalls;
    String negotiationType;
}
