package ru.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentContract {
    @PostMapping
    PaymentDto initiatePayment(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalOrderAmount(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    void emulateSuccessfulPayment(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal calculateProductsTotal(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/failed")
    void emulatePaymentDeclined(@RequestBody UUID paymentId);
}
