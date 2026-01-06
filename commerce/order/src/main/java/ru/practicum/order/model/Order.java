package ru.practicum.order.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import ru.practicum.interaction.api.enums.order.OrderState;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @UuidGenerator
    UUID orderId;

    @Column(nullable = false)
    UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(name = "order_product", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @Builder.Default
    Map<UUID, Long> products = new HashMap<>();

    UUID paymentId;

    UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    OrderState state = OrderState.NEW;

    @Column(precision = 10, scale = 3)
    BigDecimal deliveryWeight;

    @Column(precision = 10, scale = 3)
    BigDecimal deliveryVolume;

    Boolean fragile;

    @Column(precision = 10, scale = 2)
    BigDecimal totalPrice;

    @Column(precision = 10, scale = 2)
    BigDecimal deliveryPrice;

    @Column(precision = 10, scale = 2)
    BigDecimal productPrice;

    @Column(nullable = false, unique = true, length = 50)
    String username;
}
