package ru.practicum.order.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.order.ProductReturnRequest;
import ru.practicum.interaction.api.feign.contract.OrderContract;
import ru.practicum.order.service.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController implements OrderContract {
    OrderService orderService;

    @Override
    public Page<OrderDto> getClientOrders(@RequestParam @NotBlank String username, Pageable pageable) {
        return orderService.getClientOrders(username, pageable);
    }

    @Override
    public OrderDto createOrder(@RequestBody @Valid CreateNewOrderRequest newOrderRequest) {
        return orderService.createOrder(newOrderRequest);
    }

    @Override
    public OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest productReturnRequest) {
        return orderService.returnOrder(productReturnRequest);
    }

    @Override
    public OrderDto payOrder(@RequestBody UUID orderId) {
        return orderService.payOrder(orderId);
    }

    @Override
    public OrderDto updateOrderStatusAfterPaymentFailure(@RequestBody UUID orderId) {
        return orderService.updateOrderStatusAfterPaymentFailure(orderId);
    }

    @Override
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        return orderService.deliveryOrder(orderId);
    }

    @Override
    public OrderDto updateOrderStatusToDeliveryFailed(@RequestBody UUID orderId) {
        return orderService.updateOrderStatusToDeliveryFailed(orderId);
    }

    @Override
    public OrderDto completeOrder(@RequestBody UUID orderId) {
        return orderService.completeOrder(orderId);
    }

    @Override
    public OrderDto calculateOrderTotal(@RequestBody UUID orderId) {
        return orderService.calculateOrderTotal(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembleOrder(@RequestBody UUID orderId) {
        return orderService.assembleOrder(orderId);
    }

    @Override
    public OrderDto updateOrderStatusToAssemblyFailed(@RequestBody UUID orderId) {
        return orderService.updateOrderStatusToAssemblyFailed(orderId);
    }
}
