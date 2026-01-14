package ru.practicum.payment.model;

import jakarta.persistence.*;
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

    UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    PaymentState paymentState = PaymentState.PENDING;

    @Column(precision = 10, scale = 2)
    BigDecimal totalPayment;

    @Column(precision = 10, scale = 2)
    BigDecimal deliveryTotal;

    @Column(precision = 10, scale = 2)
    BigDecimal feeTotal;
}
