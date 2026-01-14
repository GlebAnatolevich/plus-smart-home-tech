package ru.practicum.warehouse.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProductToWarehouse(@Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);

    BookedProductsDto checkProductQuantityInWarehouse(@Valid ShoppingCartDto shoppingCartDto);

    AddressDto getWarehouseAddress();

    void updateProductToWarehouse(@Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    void shippedToDelivery(@Valid ShippedToDeliveryRequestDto shippedToDeliveryRequestDto);

    void returnProductsToWarehouse(@NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> returnProducts);

    void assemblyOrderProducts(@Valid AssemblyProductsForOrderRequestDto assemblyProductsForOrderRequest);
}
