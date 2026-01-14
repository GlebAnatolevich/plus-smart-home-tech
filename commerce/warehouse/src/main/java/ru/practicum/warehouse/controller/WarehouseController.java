package ru.practicum.warehouse.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;
import ru.practicum.interaction.api.feign.contract.WarehouseContract;
import ru.practicum.warehouse.service.WarehouseServiceImpl;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/warehouse")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseController implements WarehouseContract {
    WarehouseServiceImpl warehouseService;

    @Override
    public void addNewProductToWarehouse(@Valid @RequestBody NewProductInWarehouseRequestDto newProductInWarehouseRequestDto) {
        warehouseService.addNewProductToWarehouse(newProductInWarehouseRequestDto);
    }

    @Override
    public BookedProductsDto checkProductQuantityInWarehouse(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantityInWarehouse(shoppingCartDto);
    }

    @Override
    public void updateProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequestDto addProductToWarehouseRequestDto) {
        warehouseService.updateProductToWarehouse(addProductToWarehouseRequestDto);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

    @Override
    public void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequestDto shippedToDeliveryRequestDto) {
        warehouseService.shippedToDelivery(shippedToDeliveryRequestDto);
    }

    @Override
    public void returnProductsToWarehouse(@RequestBody @NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> returnProducts) {
        warehouseService.returnProductsToWarehouse(returnProducts);
    }

    @Override
    public void assemblyOrderProducts(@Valid @RequestBody AssemblyProductsForOrderRequestDto assemblyProductsForOrderRequest) {
        warehouseService.assemblyOrderProducts(assemblyProductsForOrderRequest);
    }
}
