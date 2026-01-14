package ru.practicum.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.practicum.delivery.model.Address;
import ru.practicum.delivery.model.Delivery;
import ru.practicum.interaction.api.dto.AddressDto;
import ru.practicum.interaction.api.dto.delivery.DeliveryDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeliveryMapper {
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "toAddressEntity")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "toAddressEntity")
    Delivery toDelivery(DeliveryDto dto);

    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "toAddressDto")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "toAddressDto")
    DeliveryDto toDeliveryDto(Delivery entity);

    @Named("toAddressEntity")
    @Mapping(target = "addressId", ignore = true)
    Address toAddress(AddressDto dto);

    @Named("toAddressDto")
    AddressDto toAddressDto(Address entity);
}
