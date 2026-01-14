package ru.practicum.shopping.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.feign.contract.CartContract;
import ru.practicum.shopping.cart.service.CartService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController implements CartContract {
    CartService cartService;

    @Override
    public ShoppingCartDto getCart(@RequestParam @NotBlank String username) {
        return cartService.getCart(username);
    }

    @Override
    public ShoppingCartDto addProduct(@RequestParam @NotBlank String username,
                                      @RequestBody @NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> products) {
        return cartService.addProduct(username, products);
    }

    @Override
    public void deactivateCart(@RequestParam @NotBlank String username) {
        cartService.deactivateCart(username);
    }

    @Override
    public ShoppingCartDto deleteProduct(@RequestParam @NotBlank String username,
                                         @RequestBody @NotEmpty Set<@NotNull UUID> products) {
        return cartService.deleteProduct(username, products);
    }

    @Override
    public ShoppingCartDto updateProductQuantity(@RequestParam @NotBlank String username,
                                                 @Valid @RequestBody ChangeProductQuantityRequestDto requestDto) {
        return cartService.updateProductQuantity(username, requestDto);
    }

    @Override
    public String getUsernameById(@PathVariable("cartId") UUID cartId) {
        return cartService.getUsernameById(cartId);
    }
}
