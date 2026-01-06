package ru.practicum.order.service;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.order.ProductReturnRequest;
import ru.practicum.interaction.api.dto.payment.PaymentDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.enums.delivery.DeliveryState;
import ru.practicum.interaction.api.enums.order.OrderState;
import ru.practicum.interaction.api.exception.*;
import ru.practicum.order.feign.client.CartClient;
import ru.practicum.order.feign.client.DeliveryClient;
import ru.practicum.order.feign.client.PaymentClient;
import ru.practicum.order.feign.client.WarehouseClient;
import ru.practicum.order.mapper.OrderMapper;
import ru.practicum.order.model.Order;
import ru.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Validated
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    CartClient cartClient;
    WarehouseClient warehouseClient;
    DeliveryClient deliveryClient;
    PaymentClient paymentClient;

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDto> getClientOrders(String username, Pageable pageable) {
        log.info("Запрос на получение заказов для пользователя: {} с параметрами пагинации: {}", username, pageable);
        checkUsername(username);
        Page<Order> ordersPage = orderRepository.findAllByUsername(username, pageable);
        log.info("Найдено {} заказов для пользователя {} (страница {} из {}).",
                ordersPage.getTotalElements(), username, ordersPage.getNumber() + 1, ordersPage.getTotalPages());
        return orderMapper.toOrderDtoPage(ordersPage);
    }

    @Override
    public OrderDto createOrder(@Valid CreateNewOrderRequest newOrderRequest) {
        log.info("Создание нового заказа: {}", newOrderRequest);
        String username;
        BookedProductsDto bookedProductsDto;
        AddressDto addressWarehouseDto;
        Order newOrder;

        ShoppingCartDto cartDto = newOrderRequest.getShoppingCartDto();

        try {
            username = cartClient.getUsernameById(cartDto.getShoppingCartId());
        } catch (FeignException e) {
            throw new CartOperationFailedException(
                    String.format("Не удалось получить имя пользователя из cartClient для shoppingCartId: %s",
                            cartDto.getShoppingCartId()));
        }

        try {
            bookedProductsDto = warehouseClient.checkProductQuantityInWarehouse(cartDto);
            addressWarehouseDto = warehouseClient.getWarehouseAddress();
            if (addressWarehouseDto == null) {
                throw new WarehouseOperationFailedException("Не удалось получить адрес склада.");
            }

            newOrder = orderMapper.toNewOrder(newOrderRequest, bookedProductsDto, username);
            orderRepository.save(newOrder);

        } catch (FeignException e) {
            throw new WarehouseOperationFailedException(
                    String.format("Ошибка при проверке количества товара на складе для shoppingCartId: %s, " +
                                  "количество товаров: %d", cartDto.getShoppingCartId(), cartDto.getProducts().size()));
        }

        try {
            DeliveryDto deliveryDto = DeliveryDto.builder()
                    .fromAddress(addressWarehouseDto)
                    .toAddress(newOrderRequest.getDeliveryAddress())
                    .orderId(newOrder.getOrderId())
                    .deliveryState(DeliveryState.CREATED)
                    .build();
            newOrder.setDeliveryId(deliveryClient.createDelivery(deliveryDto).getDeliveryId());
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(
                    String.format("Не удалось создать запись о доставке для заказа ID=%s.", newOrder.getOrderId()));
        }

        return orderMapper.toOrderDto(newOrder);
    }

    @Override
    public OrderDto returnOrder(@Valid ProductReturnRequest productReturnRequest) {
        log.info("Возврат товара по запросу: {}", productReturnRequest);
        Order order = getOrderById(productReturnRequest.getOrderId());

        OrderState orderStateCurrent = order.getState();
        if (orderStateCurrent == OrderState.NEW
            || orderStateCurrent == OrderState.CANCELED
            || orderStateCurrent == OrderState.PRODUCT_RETURNED) {
            throw new BadRequestException(String.format("В статусе %s невозможно вернуть заказ", orderStateCurrent));
        }

        validateReturnProducts(order, productReturnRequest);

        try {
            warehouseClient.returnProductsToWarehouse(order.getProducts());
        } catch (FeignException e) {
            throw new WarehouseOperationFailedException("Не удалось вернуть товары на склад");
        }

        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        log.info("Оплата заказа: {}", orderId);
        Order order = getOrderById(orderId);

        if (order.getState() == OrderState.PAID) {
            throw new BadRequestException("Заказ уже оплачен");
        }

        if (order.getState() == OrderState.ON_PAYMENT) {
            order.setState(OrderState.PAID);
            return orderMapper.toOrderDto(order);
        }

        if (!order.getState().equals(OrderState.ASSEMBLED)) {
            throw new BadRequestException(String.format("Заказ с ID= %s еще не собран", orderId));
        }

        try {
            order.setState(OrderState.ON_PAYMENT);
            PaymentDto paymentDto = paymentClient.initiatePayment(orderMapper.toOrderDto(order));
            order.setPaymentId(paymentDto.getPaymentId());
        } catch (FeignException e) {
            throw new PaymentOperationFailedException(String.format("Не удалось отправить заказ с ID= %s на оплату", order));
        }
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto updateOrderStatusAfterPaymentFailure(UUID orderId) {
        log.info("Неудачная попытка оплаты заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        log.info("Доставка заказа: {}", orderId);

        Order order = getOrderById(orderId);

        if (order.getState() == OrderState.ON_DELIVERY) {
            order.setState(OrderState.DELIVERED);
            return orderMapper.toOrderDto(order);
        }

        if (order.getState() != OrderState.PAID) {
            throw new BadRequestException("Заказ не был оплачен");
        }

        try {
            deliveryClient.emulateItemPickup(order.getDeliveryId());
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(
                    String.format("Не удалось передать заказ в доставку с ID=%s.", orderId));
        }

        order.setState(OrderState.ON_DELIVERY);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto updateOrderStatusToDeliveryFailed(UUID orderId) {
        log.info("Ошибка доставки заказа: {}", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateOrderTotal(UUID orderId) {
        log.info("Расчёт итоговой стоимости заказа: {}", orderId);

        Order order = getOrderById(orderId);

        try {
            BigDecimal productsPrice = paymentClient.calculateProductsTotal(orderMapper.toOrderDto(order));
            order.setProductPrice(productsPrice);

            BigDecimal totalPrice = paymentClient.calculateTotalOrderAmount(orderMapper.toOrderDto(order));
            order.setTotalPrice(totalPrice);

        } catch (FeignException e) {
            throw new PaymentOperationFailedException(String.format("Ошибка при расчете общей стоимости заказа с ID= %s", order));
        }

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчёт стоимости доставки заказа: {}", orderId);

        Order order = getOrderById(orderId);

        try {
            BigDecimal deliveryPryce = deliveryClient.calculateOrderDeliveryCost(orderMapper.toOrderDto(order));
            order.setDeliveryPrice(deliveryPryce);
        } catch (FeignException e) {
            throw new PaymentOperationFailedException(String.format("Ошибка при расчете доставки заказа с ID= %s", order));
        }

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assembleOrder(UUID orderId) {
        log.info("Сборка заказа: {}", orderId);

        Order order = getOrderById(orderId);

        if (order.getState() != OrderState.NEW) {
            throw new BadRequestException("Заказ в статусе не \"NEW\" нельзя отправить на сборку");
        }

        try {
            warehouseClient.assemblyOrderProducts(new AssemblyProductsForOrderRequestDto(orderId, order.getProducts()));
        } catch (FeignException ex) {
            throw new WarehouseOperationFailedException(
                    String.format("Не удалось отправить заказ с ID= %s на сборку", orderId));
        }

        order.setState(OrderState.ASSEMBLED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto updateOrderStatusToAssemblyFailed(UUID orderId) {
        log.info("Ошибка сборки заказа: {}", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);

        return orderMapper.toOrderDto(order);
    }

    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        }
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(String.format("Заказ с ID= %s не найден", orderId)));
    }

    private void validateReturnProducts(Order order, ProductReturnRequest returnRequest) {
        Map<UUID, Long> orderProducts = order.getProducts();
        Map<UUID, Long> returnProducts = returnRequest.getProducts();

        if (orderProducts.equals(returnProducts)) {
            return;
        }

        List<String> missingInReturn = orderProducts.keySet().stream()
                .filter(productId -> !returnProducts.containsKey(productId))
                .map(UUID::toString)
                .collect(Collectors.toList());

        List<String> extraInReturn = returnProducts.keySet().stream()
                .filter(productId -> !orderProducts.containsKey(productId))
                .map(UUID::toString)
                .collect(Collectors.toList());

        List<String> quantityMismatches = orderProducts.entrySet().stream()
                .filter(entry -> {
                    UUID productId = entry.getKey();
                    Long orderedQty = entry.getValue();
                    Long returnQty = returnProducts.get(productId);
                    return returnQty != null && !orderedQty.equals(returnQty);
                })
                .map(entry -> String.format("ID=%s: заказано %d, к возврату %d",
                        entry.getKey(), entry.getValue(), returnProducts.get(entry.getKey())))
                .collect(Collectors.toList());

        String errorMessage = String.format(
                "Несоответствие списка товаров к возврату. Товаров в заказе: %d, товаров к возврату: %d. " +
                "Отсутствующие в возврате: %s. Лишние в возврате: %s. Несоответствия количества: %s.",
                orderProducts.size(), returnProducts.size(),
                missingInReturn.isEmpty() ? "нет" : "[" + String.join(", ", missingInReturn) + "]",
                extraInReturn.isEmpty() ? "нет" : "[" + String.join(", ", extraInReturn) + "]",
                quantityMismatches.isEmpty() ? "нет" : "[" + String.join(", ", quantityMismatches) + "]"
        );

        throw new BadRequestException(errorMessage);
    }
}
