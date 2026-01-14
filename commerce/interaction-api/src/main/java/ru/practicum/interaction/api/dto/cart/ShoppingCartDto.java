package ru.practicum.interaction.api.dto.cart;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    @NotNull
    UUID shoppingCartId;

    @NotNull(message = "Список продуктов не может быть null")
    @NotEmpty(message = "Список продуктов не может быть пустым")
    Map<@NotNull(message = "ID продукта не может быть null") UUID,
            @NotNull(message = "Количество не может быть null")
            @Positive(message = "Количество должно быть положительным") Long> products;
}
