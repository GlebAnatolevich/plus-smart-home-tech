package ru.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.warehouse.model.ProductStorage;

import java.util.UUID;

@Repository
public interface ProductStorageRepository extends JpaRepository<ProductStorage, UUID> {
}
