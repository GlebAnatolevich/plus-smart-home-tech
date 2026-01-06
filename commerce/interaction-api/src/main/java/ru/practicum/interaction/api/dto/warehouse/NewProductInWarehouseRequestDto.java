package ru.practicum.interaction.api.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequestDto {
    @NotNull
    UUID productId;

    Boolean fragile;

    @NotNull(message = "Размеры товара обязательны")
    @JsonProperty("dimension")
    DimensionDto dimensionDto;

    @NotNull(message = "Вес товара обязателен")
    @DecimalMin(value = "1.000", message = "Значение должно быть не менее 1")
    BigDecimal weight;
}
