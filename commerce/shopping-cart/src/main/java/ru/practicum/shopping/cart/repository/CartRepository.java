package ru.practicum.shopping.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.interaction.api.enums.cart.CartState;
import ru.practicum.shopping.cart.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUsernameAndStatus(String username, CartState status);

    Optional<Cart> findByUsername(String username);
}
