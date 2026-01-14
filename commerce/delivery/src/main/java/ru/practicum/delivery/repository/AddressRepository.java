package ru.practicum.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.delivery.model.Address;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
