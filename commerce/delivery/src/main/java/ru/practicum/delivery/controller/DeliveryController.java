package ru.practicum.delivery.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.delivery.service.DeliveryService;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.feign.contract.DeliveryContract;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/delivery")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryController implements DeliveryContract {
    DeliveryService deliveryService;

    @Override
    public DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto newDeliveryDto) {
        return deliveryService.createDelivery(newDeliveryDto);
    }

    @Override
    public void emulateSuccessfulDelivery(@RequestBody UUID orderId) {
        deliveryService.emulateSuccessfulDelivery(orderId);
    }

    @Override
    public void emulateItemPickup(@RequestBody UUID orderId) {
        deliveryService.emulateItemPickup(orderId);
    }

    @Override
    public void emulateDeliveryDeclined(@RequestBody UUID orderId) {
        deliveryService.emulateDeliveryDeclined(orderId);
    }

    @Override
    public BigDecimal calculateOrderDeliveryCost(@RequestBody @Valid OrderDto orderDto) {
        return deliveryService.calculateOrderDeliveryCost(orderDto);
    }
}
