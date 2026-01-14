package ru.practicum.warehouse.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;
import ru.practicum.interaction.api.exception.NoOrderFoundException;
import ru.practicum.interaction.api.exception.NoSpecifiedProductInWarehouseException;
import ru.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.warehouse.mapper.WarehouseMapper;
import ru.practicum.warehouse.model.OrderBooking;
import ru.practicum.warehouse.model.ProductStorage;
import ru.practicum.warehouse.repository.OrderBookingRepository;
import ru.practicum.warehouse.repository.ProductStorageRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.interaction.api.utility.AppConstants.ADDRESSES;
import static ru.practicum.interaction.api.utility.AppConstants.SCALE_VOLUME;
import static ru.practicum.interaction.api.utility.AppConstants.SCALE_WEIGHT;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Validated
public class WarehouseServiceImpl implements WarehouseService {
    WarehouseMapper warehouseMapper;
    ProductStorageRepository productStorageRepository;
    OrderBookingRepository orderBookingRepository;

    private static final SecureRandom SECURE_RANDOM_FOR_SEED = new SecureRandom();
    private static final Random RANDOM_GENERATOR = new Random(SECURE_RANDOM_FOR_SEED.nextLong());
    private static final String CURRENT_ADDRESS = ADDRESSES[RANDOM_GENERATOR.nextInt(ADDRESSES.length)];

    @Override
    public void addNewProductToWarehouse(@Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto) {
        UUID productId = newProductInWarehouseRequestDto.getProductId();
        if (productStorageRepository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(String.format("Товар с ID = %s уже заведен на склад",
                    productId));
        }

        ProductStorage productStorage = warehouseMapper.toWarehouse(newProductInWarehouseRequestDto);
        productStorageRepository.save(productStorage);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityInWarehouse(@Valid ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> products = shoppingCartDto.getProducts();
        if (products == null || products.isEmpty()) {
            return BookedProductsDto.builder()
                    .deliveryWeight(BigDecimal.ZERO)
                    .deliveryVolume(BigDecimal.ZERO)
                    .fragile(false)
                    .build();
        }

        Set<UUID> productIds = products.keySet();

        Map<UUID, ProductStorage> productsStorage = getWarehouseProducts(productIds);

        return calculateBookedProducts(products, productsStorage);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Override
    public void updateProductToWarehouse(@Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto) {
        UUID productId = addProductToWarehouseRequestDto.getProductId();

        ProductStorage productStorage = getWarehouseProduct(productId);

        productStorage.setQuantity(productStorage.getQuantity() + addProductToWarehouseRequestDto.getQuantity());
    }

    @Override
    public void shippedToDelivery(@Valid ShippedToDeliveryRequestDto shippedToDeliveryRequestDto) {
        log.info("Получен запрос на отгрузку в доставку для заказа ID: {}, ID доставки: {}",
                shippedToDeliveryRequestDto.getOrderId(), shippedToDeliveryRequestDto.getDeliveryId());

        OrderBooking orderBooking = getOrderBooking(shippedToDeliveryRequestDto.getOrderId());
        orderBooking.setDeliveryId(shippedToDeliveryRequestDto.getDeliveryId());
    }

    @Override
    public void returnProductsToWarehouse(@NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> returnProducts) {
        if (returnProducts.isEmpty()) {
            return;
        }

        Map<UUID, ProductStorage> products = getWarehouseProducts(returnProducts.keySet());

        returnProducts.forEach((productId, quantityToReturn) -> {
            ProductStorage productStorage = products.get(productId);

            if (productStorage == null) {
                throw new NoSpecifiedProductInWarehouseException(String.format(
                        "Товар с ID = %s не найден на складе", productId));
            }

            productStorage.setQuantity(productStorage.getQuantity() + quantityToReturn);
        });
    }

    @Override
    public void assemblyOrderProducts(@Valid AssemblyProductsForOrderRequestDto assemblyProductsForOrderRequest) {
        Map<UUID, Long> assemblyProducts = assemblyProductsForOrderRequest.getProducts();
        Set<UUID> productIds = assemblyProducts.keySet();
        Map<UUID, ProductStorage> productsStorage = getWarehouseProducts(productIds);

        calculateBookedProducts(assemblyProducts, productsStorage);

        assemblyProducts.forEach((productId, requestedQuantity) -> {
            ProductStorage productStorage = productsStorage.get(productId);
            long newQuantity = productStorage.getQuantity() - requestedQuantity;
            productStorage.setQuantity(newQuantity);
        });

        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(assemblyProductsForOrderRequest.getOrderId())
                .products(assemblyProducts)
                .build();
        orderBookingRepository.save(orderBooking);
    }

    private OrderBooking getOrderBooking(UUID orderId) {
        return orderBookingRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(String.format(
                        "Бронь заказа с ID = %s не найдена", orderId)));
    }

    private ProductStorage getWarehouseProduct(UUID productId) {
        return productStorageRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(String.format(
                        "Товар с ID = %s не найден на складе", productId)));
    }

    private Map<UUID, ProductStorage> getWarehouseProducts(Set<UUID> productIds) {
        return productStorageRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(ProductStorage::getProductId, Function.identity()));
    }

    private BookedProductsDto calculateBookedProducts(Map<UUID, Long> products, Map<UUID, ProductStorage> productsStorage) {
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        boolean hasFragileItems = false;

        for (Map.Entry<UUID, Long> productEntry : products.entrySet()) {
            UUID productId = productEntry.getKey();
            Long requestedQuantity = productEntry.getValue();

            ProductStorage productStorage = productsStorage.get(productId);
            if (productStorage == null) {
                throw new NoSpecifiedProductInWarehouseException(
                        String.format("Товар с ID = %s не найден на складе", productId));
            }

            if (productStorage.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        String.format("Недостаточно товара на складе. Товар ID: %s, запрошено: %d, доступно: %d",
                                productId, requestedQuantity, productStorage.getQuantity()));
            }

            BigDecimal productWeight = productStorage.getWeight();
            BigDecimal quantityBD = BigDecimal.valueOf(requestedQuantity);
            BigDecimal currentProductTotalWeight = productWeight.multiply(quantityBD);

            totalWeight = totalWeight.add(currentProductTotalWeight);

            Objects.requireNonNull(productStorage.getDimensionDto(), String.format("Размеры товара с ID %s не заданы", productId));

            BigDecimal width = productStorage.getDimensionDto().getWidth();
            BigDecimal height = productStorage.getDimensionDto().getHeight();
            BigDecimal depth = productStorage.getDimensionDto().getDepth();

            BigDecimal singleProductVolume = width.multiply(height).multiply(depth);
            BigDecimal currentProductTotalVolume = singleProductVolume.multiply(quantityBD);

            totalVolume = totalVolume.add(currentProductTotalVolume);

            if (Boolean.TRUE.equals(productStorage.getFragile())) {
                hasFragileItems = true;
            }
        }

        totalWeight = totalWeight.setScale(SCALE_WEIGHT, RoundingMode.HALF_UP);
        totalVolume = totalVolume.setScale(SCALE_VOLUME, RoundingMode.HALF_UP);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragileItems)
                .build();
    }
}
