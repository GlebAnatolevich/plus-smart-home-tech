package ru.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {
    @NotNull(message = "Общий вес доставки обязателен")
    @DecimalMin(value = "0.000")
    BigDecimal deliveryWeight;

    @NotNull(message = "Общие объём доставки обязателен")
    @DecimalMin(value = "0.000")
    BigDecimal deliveryVolume;

    @NotNull(message = "Наличие хрупких вещей в доставке обязательно к указанию")
    Boolean fragile;
}
