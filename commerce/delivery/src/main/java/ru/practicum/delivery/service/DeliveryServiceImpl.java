package ru.practicum.delivery.service;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.delivery.feign.client.OrderClient;
import ru.practicum.delivery.feign.client.WarehouseClient;
import ru.practicum.delivery.mapper.DeliveryMapper;
import ru.practicum.delivery.model.Address;
import ru.practicum.delivery.model.Delivery;
import ru.practicum.delivery.repository.AddressRepository;
import ru.practicum.delivery.repository.DeliveryRepository;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;
import ru.practicum.interaction.api.enums.delivery.DeliveryState;
import ru.practicum.interaction.api.exception.DeliveryOperationFailedException;
import ru.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.practicum.interaction.api.exception.NotFoundException;
import ru.practicum.interaction.api.utility.AppConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static ru.practicum.interaction.api.utility.AppConstants.ADDRESSES;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {
    DeliveryMapper deliveryMapper;
    DeliveryRepository deliveryRepository;
    OrderClient orderClient;
    WarehouseClient warehouseClient;
    AddressRepository addressRepository;

    @Override
    public DeliveryDto createDelivery(@Valid DeliveryDto newDeliveryDto) {
        log.info("Начало создания новой доставки для заказа с ID: {}", newDeliveryDto.getOrderId());

        Delivery delivery = deliveryMapper.toDelivery(newDeliveryDto);

        delivery.setFromAddress(resolveAddress(newDeliveryDto.getFromAddress()));
        delivery.setToAddress(resolveAddress(newDeliveryDto.getToAddress()));

        delivery.setDeliveryState(DeliveryState.CREATED);

        return deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void emulateSuccessfulDelivery(UUID orderId) {
        log.info("Начало эмуляции успешной доставки для заказа с ID: {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);

        try {
            orderClient.completeOrder(delivery.getOrderId());
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(String.format("Не удалось завершить заказ с ID= %s", orderId));
        }

        delivery.setDeliveryState(DeliveryState.DELIVERED);
    }

    @Override
    public void emulateItemPickup(UUID orderId) {
        log.info("Начало эмуляции передачи товаров в доставку для заказа с ID: {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);

        try {
            warehouseClient.shippedToDelivery(new ShippedToDeliveryRequestDto(delivery.getOrderId(), delivery.getDeliveryId()));
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(String.format("Не удалось уведомить склад об отправке в доставку для заказа с ID= %s", orderId));
        }

        try {
            orderClient.assembleOrder(delivery.getOrderId());
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(String.format("Не удалось перевести заказ с ID=%s в статус сборки", orderId));
        }

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
    }

    @Override
    public void emulateDeliveryDeclined(UUID orderId) {
        log.info("Начало эмуляции неудачного вручения товара для заказа с ID: {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);

        try {
            orderClient.updateOrderStatusToDeliveryFailed(delivery.getOrderId());
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(
                    String.format("Не удалось уведомить сервис заказов о неудачной доставке заказа с ID= %s", orderId));
        }

        delivery.setDeliveryState(DeliveryState.FAILED);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal calculateOrderDeliveryCost(@Valid OrderDto orderDto) {
        UUID orderId = orderDto.getOrderId();
        log.info("Начало расчета стоимости доставки для заказа ID: {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);
        String warehouseStreetAddress;

        try {
            warehouseStreetAddress = warehouseClient.getWarehouseAddress().getStreet();
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(String.format("Не удалось получить адрес склада для заказа с ID= %s", orderId));
        }

        String deliveryStreetAddress = (delivery.getToAddress() != null) ? delivery.getToAddress().getStreet() : null;

        log.debug("Данные для расчета: склад на улице='{}', улица доставки='{}'", warehouseStreetAddress, deliveryStreetAddress);

        BigDecimal totalCost = AppConstants.BASE_DELIVERY_RATE;
        log.debug("1. Базовая стоимость: {}", totalCost);

        BigDecimal warehouseCharge = BigDecimal.ZERO;

        if (warehouseStreetAddress.contains(ADDRESSES[0])) {
            warehouseCharge = AppConstants.BASE_DELIVERY_RATE.multiply(AppConstants.WAREHOUSE_1_ADDRESS_MULTIPLIER);
        } else if (warehouseStreetAddress.contains(ADDRESSES[1])) {
            warehouseCharge = AppConstants.BASE_DELIVERY_RATE.multiply(AppConstants.WAREHOUSE_2_ADDRESS_MULTIPLIER);
        }

        totalCost = totalCost.add(warehouseCharge);
        log.debug("2. Стоимость после учета склада '{}' ({} + {}): {}",
                warehouseStreetAddress, AppConstants.BASE_DELIVERY_RATE, warehouseCharge, totalCost);

        if (orderDto.getFragile() != null && orderDto.getFragile()) {
            BigDecimal fragileCharge = totalCost.multiply(AppConstants.FRAGILE_MULTIPLIER);
            totalCost = totalCost.add(fragileCharge);
            log.debug("3. Стоимость после надбавки за хрупкость ({} + {}): {}",
                    totalCost.subtract(fragileCharge), fragileCharge, totalCost);
        }

        if (orderDto.getDeliveryWeight() != null) {
            BigDecimal weightCharge = orderDto.getDeliveryWeight().multiply(AppConstants.WEIGHT_MULTIPLIER);
            totalCost = totalCost.add(weightCharge);
            log.debug("4. Стоимость после надбавки за вес ({} + {}): {}",
                    totalCost.subtract(weightCharge), weightCharge, totalCost);
        }

        if (orderDto.getDeliveryVolume() != null) {
            BigDecimal volumeCharge = orderDto.getDeliveryVolume().multiply(AppConstants.VOLUME_MULTIPLIER);
            totalCost = totalCost.add(volumeCharge);
            log.debug("5. Стоимость после надбавки за объем ({} + {}): {}",
                    totalCost.subtract(volumeCharge), volumeCharge, totalCost);
        }

        if (deliveryStreetAddress != null && !deliveryStreetAddress.equals(warehouseStreetAddress)) {
            BigDecimal streetCharge = totalCost.multiply(AppConstants.STREET_MULTIPLIER);
            totalCost = totalCost.add(streetCharge);
            log.debug("6. Стоимость после надбавки за адрес ({} + {}): {}",
                    totalCost.subtract(streetCharge), streetCharge, totalCost);
        } else {
            log.debug("6. Адрес доставки совпадает со складом или не указан. Надбавка не применяется.");
        }

        log.info("Итоговая стоимость доставки для заказа ID {}: {}", orderId, totalCost);

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException(
                        String.format("Доставка для заказа с ID= %s не найдена", orderId)));
    }

    private Address resolveAddress(AddressDto dto) {
        if (dto.getAddressId() != null) {
            return addressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new NotFoundException(String.format("Адрес не найден: %s", dto.getAddressId())));
        } else {
            return deliveryMapper.toAddress(dto);
        }
    }
}
