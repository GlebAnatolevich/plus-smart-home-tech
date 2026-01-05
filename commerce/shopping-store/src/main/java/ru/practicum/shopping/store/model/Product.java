package ru.practicum.shopping.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import ru.practicum.interaction.api.enums.store.ProductCategory;
import ru.practicum.interaction.api.enums.store.ProductState;
import ru.practicum.interaction.api.enums.store.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Table(name = "product")
public class Product {
    @Id
    @UuidGenerator
    UUID productId;

    @NotBlank(message = "Наименование товара не может быть пустым")
    @Column(nullable = false)
    String productName;

    @NotBlank(message = "Описание товара не может быть пустым")
    @Column(nullable = false)
    String description;

    String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать остаток товара")
    QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать статус товара")
    ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать категорию товара")
    ProductCategory productCategory;

    @Column(nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "1.00", message = "Минимальная стоимость товара 1 руб 00 коп")
    @NotNull(message = "Необходимо указать цену товара")
    BigDecimal price;
}
