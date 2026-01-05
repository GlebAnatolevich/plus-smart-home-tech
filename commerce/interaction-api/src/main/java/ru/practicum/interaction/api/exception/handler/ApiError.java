package ru.practicum.interaction.api.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    ReasonError cause;
    List<StackTraceItem> stackTrace;
    String httpStatus;
    String userMessage;
    String message;
    List<ReasonError> suppressed;
    String localizedMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReasonError {
        List<StackTraceItem> stackTrace;
        String message;
        String localizedMessage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StackTraceItem {
        String classLoaderName;
        String moduleName;
        String moduleVersion;
        String methodName;
        String fileName;
        Integer lineNumber;
        String className;
        Boolean nativeMethod;
    }
}
