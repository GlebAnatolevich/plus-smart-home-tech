package ru.practicum.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.api.dto.warehouse.DimensionDto;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_storage")
public class ProductStorage {
    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    UUID productId;

    @Column(name = "fragile")
    Boolean fragile;

    @Embedded
    DimensionDto dimensionDto;

    @Column(name = "weight", nullable = false, precision = 10, scale = 3)
    BigDecimal weight;

    @Column(name = "quantity")
    Long quantity = 0L;
}
