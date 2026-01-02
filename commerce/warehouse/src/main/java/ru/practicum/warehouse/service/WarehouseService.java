package ru.practicum.warehouse.service;

import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;

public interface WarehouseService {
    void addNewProductToWarehouse(NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);

    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCartDto);

    AddressDto getWarehouseAddress();

    void updateProductToWarehouse(AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);
}
