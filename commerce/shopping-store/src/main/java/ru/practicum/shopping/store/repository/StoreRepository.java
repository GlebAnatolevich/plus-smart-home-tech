package ru.practicum.shopping.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.shopping.store.model.Product;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);
}
