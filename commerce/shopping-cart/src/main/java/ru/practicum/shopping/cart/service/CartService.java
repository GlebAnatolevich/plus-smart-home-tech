package ru.practicum.shopping.cart.service;

import jakarta.validation.Valid;
import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartService {
    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProduct(String username, Map<UUID, Long> products);

    void deactivateCart(String username);

    ShoppingCartDto deleteProduct(String username, Set<UUID> request);

    ShoppingCartDto updateProductQuantity(String username, @Valid ChangeProductQuantityRequestDto requestDto);

    String getUsernameById(UUID cartId);
}
