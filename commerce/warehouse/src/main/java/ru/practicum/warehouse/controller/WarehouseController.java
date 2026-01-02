package ru.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.interaction.api.feign.contract.WarehouseContract;
import ru.practicum.warehouse.service.WarehouseServiceImpl;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/warehouse")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseController implements WarehouseContract {
    WarehouseServiceImpl warehouseService;

    @Override
    @PutMapping
    public void addNewProductToWarehouse(@RequestBody @Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto) {
        warehouseService.addNewProductToWarehouse(newProductInWarehouseRequestDto);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityInWarehouse(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantityInWarehouse(shoppingCartDto);
    }

    @Override
    @PostMapping("/add")
    public void updateProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto) {  // Добавил @Valid, если нужно
        warehouseService.updateProductToWarehouse(addProductToWarehouseRequestDto);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}
