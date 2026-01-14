package ru.practicum.interaction.api.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.dto.cart.ShoppingCartDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class CreateNewOrderRequest {
    @NotNull
    @Valid
    ShoppingCartDto shoppingCartDto;

    @NotNull
    @Valid
    AddressDto deliveryAddress;
}
