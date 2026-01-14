package ru.practicum.interaction.api.dto.warehouse;

import jakarta.persistence.Embeddable;
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
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotNull(message = "Необходимо указать ширину")
    @DecimalMin(value = "1.000", message = "Значение должно быть не менее 1")
    BigDecimal width;

    @NotNull(message = "Необходимо указать высоту")
    @DecimalMin(value = "1.000", message = "Значение должно быть не менее 1")
    BigDecimal height;

    @NotNull(message = "Необходимо указать глубину")
    @DecimalMin(value = "1.000", message = "Значение должно быть не менее 1")
    BigDecimal depth;
}
