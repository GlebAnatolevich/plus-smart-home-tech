package ru.practicum.shopping.store.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.QuantityState;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto createProduct(@Valid ProductDto newProductDto);

    ProductDto updateProduct(@Valid ProductDto updateProductDto);

    boolean deleteProduct(UUID productId);

    boolean updateQuantityState(UUID productId, QuantityState quantityState);

    ProductDto getProductById(UUID productId);

    List<ProductDto> getProductsByIds(List<UUID> productIds);
}
