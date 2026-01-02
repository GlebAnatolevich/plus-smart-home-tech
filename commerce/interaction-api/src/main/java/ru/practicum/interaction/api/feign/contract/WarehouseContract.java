package ru.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;

public interface WarehouseContract {
    void addNewProductToWarehouse(@RequestBody @Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);

    BookedProductsDto checkProductQuantityInWarehouse(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    void updateProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    AddressDto getWarehouseAddress();
}
