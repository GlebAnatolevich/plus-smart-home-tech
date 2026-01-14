package ru.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartContract {
    @GetMapping
    ShoppingCartDto getCart(@RequestParam @NotBlank String username);

    @PutMapping
    ShoppingCartDto addProduct(@RequestParam @NotBlank String username,
                               @RequestBody @NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> products);

    @DeleteMapping
    void deactivateCart(@RequestParam @NotBlank String username);

    @PostMapping("/remove")
    ShoppingCartDto deleteProduct(@RequestParam @NotBlank String username,
                                  @RequestBody @NotEmpty Set<@NotNull UUID> products);

    @PostMapping("/change-quantity")
    ShoppingCartDto updateProductQuantity(@RequestParam @NotBlank String username,
                                          @Valid @RequestBody ChangeProductQuantityRequestDto request);

    @GetMapping("/name/{cartId}")
    String getUsernameById(@PathVariable("cartId") UUID cartId);
}
