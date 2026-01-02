package ru.practicum.shopping.cart.service;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.interaction.api.enums.cart.CartState;
import ru.practicum.interaction.api.exception.BadRequestException;
import ru.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.practicum.interaction.api.exception.NotFoundException;
import ru.practicum.shopping.cart.feign.client.WarehouseClient;
import ru.practicum.shopping.cart.mapper.CartMapper;
import ru.practicum.shopping.cart.model.Cart;
import ru.practicum.shopping.cart.repository.CartRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(String username) {
        checkUsername(username);
        log.debug("Поиск корзины для пользователя: {} ", username);

        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.debug("Корзина для пользователя {} не найдена. Создаю новую.", username);
                    return Cart.builder()
                            .username(username)
                            .status(CartState.ACTIVE)
                            .products(new HashMap<>())
                            .build();
                });
        return cartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> products) {
        checkUsername(username);
        log.info("Запрос на добавление товаров в корзину пользователя: {}. Количество товаров: {}", username, products.size());

        if (products.isEmpty()) {
            throw new BadRequestException("Список продуктов для добавления не может быть пустым");
        }

        log.debug("Поиск корзины для пользователя: {} ", username);

        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.debug("Корзина для пользователя {} не найдена. Создаю новую.", username);
                    return Cart.builder()
                            .username(username)
                            .status(CartState.ACTIVE)
                            .products(new HashMap<>())
                            .build();
                });

        cart = cartRepository.save(cart);

        updateCartProducts(cart, products);

        try {
            log.info("проверяю наличие на складе: id = {}, products = {}", cart.getShoppingCartId(), cart.getProducts());

            BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityInWarehouse(cartMapper.toDto(cart));

            log.info("Проверено наличие на складе: {}", bookedProductsDto);

        } catch (FeignException e) {
            log.error("Ошибка вызова склада: {}", e.getMessage());
            throw new RuntimeException("Склад не доступен", e);
        }

        cart = cartRepository.save(cart);

        ShoppingCartDto result = cartMapper.toDto(cart);

        log.debug("Товары добавлены в корзину пользователя: {}. Итоговое количество товаров: {}",
                username, result.getProducts().size());

        return result;
    }

    @Override
    public void deactivateCart(String username) {
        checkUsername(username);
        log.info("Запрос на деактивацию корзины для пользователя: {}", username);

        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Корзина для пользователя %s не найдена", username)));

        if (cart.getStatus() != CartState.DEACTIVATE) {
            cart.setStatus(CartState.DEACTIVATE);
            cartRepository.save(cart);
            log.info("Корзина пользователя {} деактивирована", username);
        } else {
            log.debug("Корзина уже деактивирована для пользователя: {}", username);
        }
    }

    @Override
    public ShoppingCartDto deleteProduct(String username, Set<UUID> request) {
        checkUsername(username);

        if (request == null || request.isEmpty()) {
            throw new BadRequestException("Список товаров для удаления не может быть пустым или null");
        }

        Cart cart = cartRepository.findByUsernameAndStatus(username, CartState.ACTIVE)
                .orElseThrow(() -> new NotFoundException(String.format("Активной корзины покупок " +
                                                                       "для пользователя %s не найдено", username)));

        if (cart.getProducts() != null) {
            cart.getProducts().keySet().removeAll(request);
        }

        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequestDto requestDto) {
        checkUsername(username);

        if (requestDto == null) throw new BadRequestException("Запрос на обновление не может быть null");

        Cart cart = cartRepository.findByUsernameAndStatus(username, CartState.ACTIVE)
                .orElseThrow(() -> new NotFoundException(String.format("Активной корзины покупок " +
                                                                       "для пользователя %s не найдено", username)));

        UUID productId = requestDto.getProductId();
        Long newQuantity = requestDto.getNewQuantity();

        Map<UUID, Long> products = cart.getProducts();

        if (products == null) {
            products = new HashMap<>();
            cart.setProducts(products);
        }

        if (!products.containsKey(productId)) {
            throw new BadRequestException(String.format("Товар с ID %s отсутствует в корзине", productId));
        }

        if (newQuantity == 0) {
            products.remove(productId);
        } else {
            products.put(productId, newQuantity);
        }

        return cartMapper.toDto(cartRepository.save(cart));
    }

    private void checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        }
    }

    private void updateCartProducts(Cart cart, Map<UUID, Long> newProducts) {
        if (cart.getProducts() == null) {
            cart.setProducts(new HashMap<>());
        }
        newProducts.forEach((productId, quantity) ->
                cart.getProducts().merge(productId, quantity, Long::sum));
    }
}
