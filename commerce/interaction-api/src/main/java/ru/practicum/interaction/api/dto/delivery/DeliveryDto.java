package ru.practicum.interaction.api.dto.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.enums.delivery.DeliveryState;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    UUID deliveryId;

    @NotNull
    @Valid
    AddressDto fromAddress;

    @NotNull
    @Valid
    AddressDto toAddress;

    @NotNull
    UUID orderId;

    @NotNull
    DeliveryState deliveryState;
}
