package ru.practicum.payment.service;

import jakarta.validation.Valid;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto initiatePayment(@Valid OrderDto orderDto);

    BigDecimal calculateTotalOrderAmount(@Valid OrderDto orderDto);

    void emulateSuccessfulPayment(UUID orderId);

    BigDecimal calculateProductsTotal(@Valid OrderDto orderDto);

    void emulatePaymentDeclined(UUID orderId);
}
