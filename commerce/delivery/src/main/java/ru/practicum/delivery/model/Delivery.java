package ru.practicum.delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import ru.practicum.interaction.api.enums.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {
    @Id
    @UuidGenerator
    UUID deliveryId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "from_address_id", nullable = false)
    @NotNull(message = "Адрес отправителя обязателен")
    Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "to_address_id", nullable = false)
    @NotNull(message = "Адрес получателя обязателен")
    Address toAddress;

    @Column(nullable = false)
    @NotNull(message = "Идентификатор orderId обязателен")
    UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Статус доставки обязателен")
    @Builder.Default
    DeliveryState deliveryState = DeliveryState.CREATED;
}