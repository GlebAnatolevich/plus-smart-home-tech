package ru.practicum.interaction.api.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    @Min(value = 1, message = "Минимальное значение 1")
    Double weight;
}
