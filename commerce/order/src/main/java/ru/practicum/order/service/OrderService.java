package ru.practicum.order.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.order.ProductReturnRequest;

import java.util.UUID;

public interface OrderService {
    Page<OrderDto> getClientOrders(String username, Pageable pageable);

    OrderDto createOrder(@Valid CreateNewOrderRequest newOrderRequest);

    OrderDto returnOrder(@Valid ProductReturnRequest productReturnRequest);

    OrderDto payOrder(UUID orderId);

    OrderDto updateOrderStatusAfterPaymentFailure(UUID orderId);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto updateOrderStatusToDeliveryFailed(UUID orderId);

    OrderDto completeOrder(UUID orderId);

    OrderDto calculateOrderTotal(UUID orderId);

    OrderDto calculateDeliveryCost(UUID orderId);

    OrderDto assembleOrder(UUID orderId);

    OrderDto updateOrderStatusToAssemblyFailed(UUID orderId);
}
