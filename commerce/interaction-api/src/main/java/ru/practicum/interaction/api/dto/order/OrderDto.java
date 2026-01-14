package ru.practicum.interaction.api.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.api.enums.order.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    @NotNull
    UUID orderId;

    @NotNull
    UUID shoppingCartId;

    @NotNull(message = "Список продуктов не может быть null")
    @NotEmpty(message = "Список продуктов не может быть пустым")
    Map<@NotNull(message = "ID продукта не может быть null") UUID,
            @NotNull(message = "Количество не может быть null")
            @Positive(message = "Количество должно быть положительным") Long> products;

    UUID paymentId;

    UUID deliveryId;

    OrderState state;

    @DecimalMin(value = "0.000")
    BigDecimal deliveryWeight;

    @DecimalMin(value = "0.000")
    BigDecimal deliveryVolume;

    Boolean fragile;

    @DecimalMin(value = "0.00")
    BigDecimal totalPrice;

    @DecimalMin(value = "0.00")
    BigDecimal deliveryPrice;

    @DecimalMin(value = "0.00")
    BigDecimal productPrice;
}
