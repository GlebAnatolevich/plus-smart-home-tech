package ru.practicum.interaction.api.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.interaction.api.exception.BadRequestException;
import ru.practicum.interaction.api.exception.CartOperationFailedException;
import ru.practicum.interaction.api.exception.DeliveryOperationFailedException;
import ru.practicum.interaction.api.exception.NoCartFoundException;
import ru.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.practicum.interaction.api.exception.NoOrderFoundException;
import ru.practicum.interaction.api.exception.NoPaymentFoundException;
import ru.practicum.interaction.api.exception.NoSpecifiedProductInWarehouseException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.exception.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.interaction.api.exception.NotFoundException;
import ru.practicum.interaction.api.exception.OrderOperationFailedException;
import ru.practicum.interaction.api.exception.PaymentOperationFailedException;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.interaction.api.exception.StoreOperationFailedException;
import ru.practicum.interaction.api.exception.WarehouseOperationFailedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final Map<Class<? extends Exception>, String> NOT_FOUND_MESSAGES = new HashMap<>();

    static {
        NOT_FOUND_MESSAGES.put(ProductNotFoundException.class, "Товар не найден. Пожалуйста, проверьте запрос.");
        NOT_FOUND_MESSAGES.put(NoPaymentFoundException.class, "Платёж не найден. Пожалуйста, проверьте запрос.");
        NOT_FOUND_MESSAGES.put(NoDeliveryFoundException.class, "Информация по доставке не найдена. Пожалуйста, проверьте запрос.");
        NOT_FOUND_MESSAGES.put(NoCartFoundException.class, "Корзина не найдена. Пожалуйста, проверьте запрос.");
        NOT_FOUND_MESSAGES.put(NoOrderFoundException.class, "Заказ не найден. Пожалуйста, проверьте запрос.");
        NOT_FOUND_MESSAGES.put(NotFoundException.class, "Объект не найден. Пожалуйста, проверьте запрос.");
    }

    @ExceptionHandler({
            ProductNotFoundException.class,
            NoPaymentFoundException.class,
            NoDeliveryFoundException.class,
            NoCartFoundException.class,
            NoOrderFoundException.class,
            NotFoundException.class
    })

    public ResponseEntity<ApiError> handleNotFoundExceptions(Exception ex) {
        String errorMessage = NOT_FOUND_MESSAGES.get(ex.getClass());

        log.warn("Выброшено исключение {}: {}", ex.getClass().getSimpleName(), errorMessage);

        ApiError response = createResponse(ex, errorMessage);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ApiError> handleNotAuthorizedUserException(Exception ex) {
        log.warn("Выброшено исключение NotAuthorizedUserException");

        ApiError response = createResponse(ex, "Пользователь не указан. Пожалуйста, проверьте запрос.");

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    private static final Map<Class<? extends Exception>, String> BAD_REQUEST_MESSAGES = new HashMap<>();

    static {
        BAD_REQUEST_MESSAGES.put(SpecifiedProductAlreadyInWarehouseException.class,
                "Ошибка, товар с таким описанием уже зарегистрирован на складе");
        BAD_REQUEST_MESSAGES.put(NoSpecifiedProductInWarehouseException.class,
                "Нет информации о товаре на складе");
        BAD_REQUEST_MESSAGES.put(BadRequestException.class,
                "Некорректный запрос.");
        BAD_REQUEST_MESSAGES.put(WarehouseOperationFailedException.class,
                "Ошибка работы с feign client: warehouse");
        BAD_REQUEST_MESSAGES.put(CartOperationFailedException.class,
                "Ошибка работы с feign client: shopping - cart.");
        BAD_REQUEST_MESSAGES.put(PaymentOperationFailedException.class,
                "Ошибка работы с feign client: payment.");
        BAD_REQUEST_MESSAGES.put(DeliveryOperationFailedException.class,
                "Ошибка работы с feign client: delivery.");
        BAD_REQUEST_MESSAGES.put(StoreOperationFailedException.class,
                "Ошибка работы с feign client: store.");
        BAD_REQUEST_MESSAGES.put(OrderOperationFailedException.class,
                "Ошибка работы с feign client: order");
        BAD_REQUEST_MESSAGES.put(NotEnoughInfoInOrderToCalculateException.class,
                "Недостаточно информации в заказе для расчёта");
    }

    @ExceptionHandler({
            SpecifiedProductAlreadyInWarehouseException.class,
            NoSpecifiedProductInWarehouseException.class,
            BadRequestException.class,
            WarehouseOperationFailedException.class,
            CartOperationFailedException.class,
            PaymentOperationFailedException.class,
            DeliveryOperationFailedException.class,
            StoreOperationFailedException.class,
            OrderOperationFailedException.class,
            NotEnoughInfoInOrderToCalculateException.class
    })
    public ResponseEntity<ApiError> handleBadRequestExceptions(Exception ex) {
        String errorMessage = BAD_REQUEST_MESSAGES.get(ex.getClass());

        log.warn("Выброшено исключение {}: {}", ex.getClass().getSimpleName(), errorMessage);

        ApiError response = createResponse(ex, errorMessage);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ApiError createResponse(Exception ex, String message) {
        ApiError response = ApiError.builder()
                .message(ex.getMessage())
                .localizedMessage(ex.getLocalizedMessage())
                .userMessage(message)
                .httpStatus(HttpStatus.BAD_REQUEST.toString())
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
