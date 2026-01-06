package ru.practicum.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.delivery.model.Delivery;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Optional<Delivery> findByOrderId(UUID OrderId);
}
