package ru.practicum.shopping.store.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.QuantityState;

import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto createProduct(@Valid @RequestBody ProductDto newProductDto);

    ProductDto updateProduct(@Valid @RequestBody ProductDto updateProductDto);

    boolean deleteProduct(@RequestBody UUID productId);

    boolean updateQuantityState(@Valid UUID productId, QuantityState quantityState);

    ProductDto getProductById(@PathVariable UUID productId);
}
