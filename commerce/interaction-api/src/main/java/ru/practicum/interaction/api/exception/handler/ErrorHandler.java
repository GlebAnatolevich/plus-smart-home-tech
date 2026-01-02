package ru.practicum.interaction.api.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.interaction.api.exception.BadRequestException;
import ru.practicum.interaction.api.exception.NoSpecifiedProductInWarehouseException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.exception.NotFoundException;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFoundException(Exception ex) {
        log.warn("Выброшено исключение ProductNotFoundException");

        ApiError response = createResponse(ex, "Товар не найден. Пожалуйста, проверьте запрос.");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ApiError> handleNotAuthorizedUserException(Exception ex) {
        log.warn("Выброшено исключение NotAuthorizedUserException");

        ApiError response = createResponse(ex, "Пользователь не указан. Пожалуйста, проверьте запрос.");

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(Exception ex) {
        log.warn("Выброшено исключение NotFoundException");

        ApiError response = createResponse(ex, "Ресурс не найден.");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ApiError> handleSpecifiedProductAlreadyInWarehouseException(Exception ex) {
        log.warn("Выброшено исключение SpecifiedProductAlreadyInWarehouseException");

        ApiError response = createResponse(ex, "Ошибка, товар с таким описанием уже зарегистрирован на складе");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ApiError> handleNoSpecifiedProductInWarehouseException(Exception ex) {
        log.warn("Выброшено исключение NoSpecifiedProductInWarehouseException");

        ApiError response = createResponse(ex, "Нет информации о товаре на складе");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(Exception ex) {
        log.warn("Выброшено исключение BadRequestException");

        ApiError response = createResponse(ex, "Некорректный запрос.");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ApiError createResponse(Exception ex, String message) {
        ApiError response = ApiError.builder()
                .message(ex.getMessage())
                .localizedMessage(ex.getLocalizedMessage())
                .userMessage(message)
                .httpStatus(HttpStatus.NOT_FOUND.toString())
                .stackTrace(convertStackTrace(ex.getStackTrace()))
                .build();

        if (ex.getCause() != null) {
            Throwable cause = ex.getCause();
            ApiError.ReasonError reasonError = new ApiError.ReasonError(
                    convertStackTrace(cause.getStackTrace()),
                    cause.getMessage(),
                    cause.getLocalizedMessage()
            );
            response.setCause(reasonError);
        }

        if (ex.getSuppressed() != null && ex.getSuppressed().length > 0) {
            List<ApiError.ReasonError> suppressedList = Arrays.stream(ex.getSuppressed())
                    .map(sup -> new ApiError.ReasonError(
                            convertStackTrace(sup.getStackTrace()),
                            sup.getMessage(),
                            sup.getLocalizedMessage()
                    ))
                    .collect(Collectors.toList());
            response.setSuppressed(suppressedList);
        }

        return response;
    }

    private List<ApiError.StackTraceItem> convertStackTrace(StackTraceElement[] elements) {
        if (elements == null || elements.length == 0) {
            return List.of();
        }

        return Arrays.stream(elements)
                .map(el -> new ApiError.StackTraceItem(
                        el.getClassLoaderName(),
                        el.getModuleName(),
                        el.getModuleVersion(),
                        el.getMethodName(),
                        el.getFileName(),
                        el.getLineNumber(),
                        el.getClassName(),
                        el.isNativeMethod()
                ))
                .collect(Collectors.toList());
    }
}
