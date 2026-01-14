package ru.practicum.interaction.api.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {
    UUID paymentId;

    @DecimalMin(value = "0.00")
    BigDecimal totalPayment;

    @DecimalMin(value = "0.00")
    BigDecimal deliveryTotal;

    @DecimalMin(value = "0.00")
    BigDecimal feeTotal;
}
