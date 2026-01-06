package ru.practicum.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.practicum.order.model.Order;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {
    OrderDto toOrderDto(Order order);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "shoppingCartId", source = "request.shoppingCartDto.shoppingCartId")
    @Mapping(target = "products", source = "request.shoppingCartDto.products")
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "state", constant = "NEW")
    @Mapping(target = "deliveryWeight", source = "bookedProductsDto.deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "bookedProductsDto.deliveryVolume")
    @Mapping(target = "fragile", source = "bookedProductsDto.fragile")
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deliveryPrice", ignore = true)
    @Mapping(target = "productPrice", ignore = true)
    @Mapping(target = "username", source = "username")
    Order toNewOrder(CreateNewOrderRequest request, BookedProductsDto bookedProductsDto, String username);

    default Page<OrderDto> toOrderDtoPage(Page<Order> ordersPage) {
        List<OrderDto> dtos = ordersPage.getContent().stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, ordersPage.getPageable(), ordersPage.getTotalElements());
    }
}
