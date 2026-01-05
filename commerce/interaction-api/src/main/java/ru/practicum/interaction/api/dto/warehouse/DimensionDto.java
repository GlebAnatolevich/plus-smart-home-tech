package ru.practicum.interaction.api.dto.warehouse;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotNull(message = "Необходимо указать ширину")
    @Min(value = 1, message = "Минимальное значение 1")
    Double width;

    @NotNull(message = "Необходимо указать высоту")
    @Min(value = 1, message = "Минимальное значение 1")
    Double height;

    @NotNull(message = "Необходимо указать глубину")
    @Min(value = 1, message = "Минимальное значение 1")
    Double depth;
}
