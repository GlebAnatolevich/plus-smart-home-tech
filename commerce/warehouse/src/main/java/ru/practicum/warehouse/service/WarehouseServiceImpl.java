package ru.practicum.warehouse.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.exception.NoSpecifiedProductInWarehouseException;
import ru.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.warehouse.mapper.WarehouseMapper;
import ru.practicum.warehouse.model.ProductStorage;
import ru.practicum.warehouse.repository.ProductStorageRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    WarehouseMapper warehouseMapper;
    ProductStorageRepository productStorageRepository;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequestDto newProductInWarehouseRequestDto) {
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
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragileItems = false;

        for (Map.Entry<UUID, Long> productEntry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = productEntry.getKey();
            Long requestedQuantity = productEntry.getValue();

            ProductStorage productStorage = productStorageRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                            String.format(
                                    "Товар с ID = %s не найден на складе", productId)));

            if (productStorage.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        String.format("Недостаточно товара на складе. Товар ID: %s, запрошено: %d, доступно: %d",
                                productId, requestedQuantity, productStorage.getQuantity()));
            }

            totalWeight += productStorage.getWeight() * requestedQuantity;
            totalVolume += productStorage.getDimensionDto().getWidth() * productStorage.getDimensionDto().getHeight() *
                           productStorage.getDimensionDto().getDepth() * requestedQuantity;

            if (productStorage.getFragile()) {
                hasFragileItems = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragileItems)
                .build();
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
    public void updateProductToWarehouse(AddProductToWarehouseRequestDto addProductToWarehouseRequestDto) {
        UUID productId = addProductToWarehouseRequestDto.getProductId();

        ProductStorage productStorage = productStorageRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(String.format(
                        "Товар с ID = %s не найден на складе", productId)));

        productStorage.setQuantity(productStorage.getQuantity() + addProductToWarehouseRequestDto.getQuantity());
        productStorageRepository.save(productStorage);
    }
}
