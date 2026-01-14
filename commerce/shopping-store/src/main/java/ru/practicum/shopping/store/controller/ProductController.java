package ru.practicum.shopping.store.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.QuantityState;
import ru.practicum.interaction.api.feign.contract.StoreContract;
import ru.practicum.shopping.store.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-store")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController implements StoreContract {
    ProductService productService;

    @Override
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        Pageable pageable) {
        return productService.getProducts(category, pageable);
    }

    @Override
    public ProductDto createProduct(@Valid @RequestBody ProductDto newProductDto) {
        return productService.createProduct(newProductDto);
    }

    @Override
    public ProductDto updateProduct(@Valid @RequestBody ProductDto updateProductDto) {
        return productService.updateProduct(updateProductDto);
    }

    @Override
    public Boolean deleteProduct(@RequestBody UUID productId) {
        return productService.deleteProduct(productId);
    }

    @Override
    public Boolean updateQuantityState(@RequestParam UUID productId,
                                       @RequestParam QuantityState quantityState) {
        return productService.updateQuantityState(productId, quantityState);
    }

    @Override
    public ProductDto getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }

    @Override
    public List<ProductDto> getProductsByIds(@RequestBody List<UUID> productIds) {
        return productService.getProductsByIds(productIds);
    }
}
