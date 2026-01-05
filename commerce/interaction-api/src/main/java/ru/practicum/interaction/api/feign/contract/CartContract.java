package ru.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartContract {
    ShoppingCartDto getCart(@RequestParam String username);

    ShoppingCartDto addProduct(@RequestParam String username,
                               @RequestBody @NotNull Map<UUID, Long> products);

    void deactivateCart(@RequestParam String username);

    ShoppingCartDto deleteProduct(@RequestParam String username, @RequestBody Set<UUID> products);

    ShoppingCartDto updateProductQuantity(@RequestParam String username,
                                          @RequestBody @Valid ChangeProductQuantityRequestDto request);
}
