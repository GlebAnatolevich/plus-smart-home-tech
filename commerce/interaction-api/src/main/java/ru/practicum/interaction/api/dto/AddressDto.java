package ru.practicum.interaction.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {
    UUID addressId;

    @Size(max = 20)
    String country;

    @Size(max = 30)
    String city;

    @NotBlank
    @Size(max = 50)
    String street;

    @Size(max = 10)
    String house;

    @Size(max = 10)
    String flat;
}
