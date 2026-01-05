package ru.practicum.interaction.api.dto.cart;

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
public class ChangeProductQuantityRequestDto {
    @NotNull
    UUID productId;

    @NotNull(message = "Количество необходимо указать")
    @Min(value = 0, message = "Количество должно быть неотрицательным")
    Long newQuantity;
}
