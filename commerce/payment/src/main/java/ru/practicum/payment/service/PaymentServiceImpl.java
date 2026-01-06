package ru.practicum.payment.service;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.payment.PaymentDto;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.payment.PaymentState;
import ru.practicum.interaction.api.exception.*;
import ru.practicum.interaction.api.utility.AppConstants;
import ru.practicum.payment.feign.client.OrderClient;
import ru.practicum.payment.feign.client.ShoppingStoreClient;
import ru.practicum.payment.mapper.PaymentMapper;
import ru.practicum.payment.model.Payment;
import ru.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
@Validated
public class PaymentServiceImpl implements PaymentService {
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    ShoppingStoreClient shoppingStoreClient;
    OrderClient orderClient;

    @Override
    public PaymentDto initiatePayment(@Valid OrderDto orderDto) {
        log.info("Инициация платежа для заказа с ID: {}", orderDto.getOrderId());

        return paymentMapper.toPaymentDto(paymentRepository.save(paymentMapper.toPayment(orderDto)));
    }

    @Override
    public BigDecimal calculateTotalOrderAmount(@Valid OrderDto orderDto) {
        log.info("Начат расчет итоговой суммы для заказа ID: {}", orderDto.getOrderId());

        BigDecimal productPrice = Objects.requireNonNullElse(orderDto.getProductPrice(), BigDecimal.ZERO);
        BigDecimal deliveryPrice = Objects.requireNonNullElse(orderDto.getDeliveryPrice(), BigDecimal.ZERO);

        BigDecimal feeTotal = productPrice.multiply(AppConstants.NDS_RATE);
        log.debug("Рассчитан НДС (10%): {} для стоимости товаров: {}", feeTotal, productPrice);

        BigDecimal priceWithFee = productPrice.add(feeTotal);
        log.debug("Стоимость товаров с НДС: {}", priceWithFee);

        BigDecimal totalAmount = priceWithFee.add(deliveryPrice);
        log.info("Итоговая сумма (с доставкой {}): {}", deliveryPrice, totalAmount);

        return totalAmount;
    }

    @Override
    public void emulateSuccessfulPayment(UUID paymentId) {
        log.debug("Начало эмуляции успешной оплаты для платежа ID: {}", paymentId);

        Payment payment = getPaymentById(paymentId);

        UUID orderId = payment.getOrderId();

        try {
            orderClient.payOrder(orderId);
        } catch (FeignException e) {
            throw new OrderOperationFailedException(
                    String.format("Не удалось уведомить сервис заказов об успешной оплате для заказа ID= %s " +
                                  "(платеж ID= %s)", orderId, paymentId));
        }

        payment.setPaymentState(PaymentState.SUCCESS);
    }

    @Override
    public BigDecimal calculateProductsTotal(@Valid OrderDto orderDto) {
        log.info("Начало расчета общей стоимости товаров для заказа {}", orderDto.getOrderId());
        Map<UUID, Long> productsInOrder = orderDto.getProducts();

        if (productsInOrder == null || productsInOrder.isEmpty()) {
            log.warn("В заказе {} нет товаров для расчета стоимости.", orderDto.getOrderId());
            return BigDecimal.ZERO;
        }

        List<UUID> productIds = new ArrayList<>(productsInOrder.keySet());

        List<ProductDto> productInfos;
        try {
            productInfos = shoppingStoreClient.getProductsByIds(productIds);
        } catch (FeignException e) {
            throw new StoreOperationFailedException("Не удалось получить данные о товарах из сервиса-магазина.");
        }

        Map<UUID, ProductDto> productInfoMap = productInfos.stream()
                .collect(Collectors.toMap(ProductDto::getProductId, product -> product));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<UUID, Long> orderEntry : productsInOrder.entrySet()) {
            UUID productId = orderEntry.getKey();
            Long quantity = orderEntry.getValue();

            ProductDto productInfo = productInfoMap.get(productId);

            if (productInfo == null || productInfo.getPrice() == null) {
                throw new NotEnoughInfoInOrderToCalculateException(
                        String.format("Информация о товаре с ID= %s не найдена.", productId));
            }

            BigDecimal subtotal = productInfo.getPrice().multiply(new BigDecimal(quantity));
            totalPrice = totalPrice.add(subtotal);
        }

        return totalPrice;
    }

    @Override
    public void emulatePaymentDeclined(UUID paymentId) {
        log.debug("Начало эмуляции неуспешной оплаты для платежа ID: {}", paymentId);

        Payment payment = getPaymentById(paymentId);
        UUID orderId = payment.getOrderId();

        PaymentState currentPaymentState = payment.getPaymentState();

        if (currentPaymentState == PaymentState.FAILED || currentPaymentState == PaymentState.SUCCESS) {
            throw new BadRequestException(String.format("Платёж находится на неверном статусе - %s", currentPaymentState));
        }

        try {
            orderClient.updateOrderStatusAfterPaymentFailure(orderId);
        } catch (FeignException e) {
            throw new OrderOperationFailedException(
                    String.format("Не удалось уведомить сервис заказов о неуспешной оплате для заказа ID= %s " +
                                  "(платеж ID= %s)", orderId, paymentId));
        }

        payment.setPaymentState(PaymentState.FAILED);
    }

    private Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException(String.format("Платеж с ID= %s не найден", paymentId)));
    }
}