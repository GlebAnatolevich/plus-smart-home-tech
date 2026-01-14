package ru.practicum.shopping.store.service;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.ProductState;
import ru.practicum.interaction.api.enums.store.QuantityState;
import ru.practicum.interaction.api.exception.ProductNotFoundException;
import ru.practicum.shopping.store.mapper.ProductMapper;
import ru.practicum.shopping.store.model.Product;
import ru.practicum.shopping.store.repository.StoreRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Validated
public class ProductServiceImpl implements ProductService {
    StoreRepository storeRepository;
    ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = storeRepository.findAllByProductCategory(category, pageable);

        return productMapper.toDtoPage(products);
    }

    @Override
    public ProductDto createProduct(@Valid ProductDto newProductDto) {
        Product product = productMapper.toEntity(newProductDto);

        return productMapper.toDto(storeRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(@Valid ProductDto updateProductDto) {
        Product product = validateProductExist(updateProductDto.getProductId());
        productMapper.updateFromDto(updateProductDto, product);

        return productMapper.toDto(storeRepository.save(product));
    }

    @Override
    public boolean deleteProduct(UUID productId) {
        Product product = validateProductExist(productId);
        product.setProductState(ProductState.DEACTIVATE);
        storeRepository.save(product);
        return true;
    }

    @Override
    public boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        if (productId == null) {
            throw new IllegalArgumentException("productId не может быть null");
        }
        if (quantityState == null) {
            throw new IllegalArgumentException("quantityState не может быть null");
        }

        Product product = validateProductExist(productId);
        product.setQuantityState(quantityState);
        storeRepository.save(product);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        Product product = validateProductExist(productId);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByIds(List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Product> products = storeRepository.findAllById(productIds);

        return productMapper.toDtoList(products);
    }

    private Product validateProductExist(UUID productId) {
        return storeRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Товар c id = %s не найден",
                        productId)));
    }
}
