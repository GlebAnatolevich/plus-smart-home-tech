package ru.practicum.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import ru.practicum.interaction.api.enums.payment.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @UuidGenerator
    UUID paymentId;

    @NotNull
    UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NotNull
    @Builder.Default
    PaymentState paymentState = PaymentState.PENDING;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.00")
    BigDecimal totalPayment;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.00")
    BigDecimal deliveryTotal;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.00")
    BigDecimal feeTotal;
}
