package ru.practicum.shopping.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        return cartService.getCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProduct(@RequestParam String username,
                                      @RequestBody @NotNull Map<UUID, Long> products) {
        return cartService.addProduct(username, products);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam String username) {
        cartService.deactivateCart(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto deleteProduct(@RequestParam String username,
                                         @RequestBody Set<UUID> request) {
        return cartService.deleteProduct(username, request);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto updateProductQuantity(@RequestParam String username,
                                                 @RequestBody @Valid ChangeProductQuantityRequestDto requestDto) {
        return cartService.updateProductQuantity(username, requestDto);
    }
}
