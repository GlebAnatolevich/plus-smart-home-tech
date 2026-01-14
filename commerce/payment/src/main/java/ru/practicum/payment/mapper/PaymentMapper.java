package ru.practicum.payment.mapper;

import org.mapstruct.*;
import ru.practicum.interaction.api.dto.order.OrderDto;
import ru.practicum.interaction.api.dto.payment.PaymentDto;
import ru.practicum.interaction.api.utility.AppConstants;
import ru.practicum.payment.model.Payment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = {AppConstants.class})
public interface PaymentMapper {
    PaymentDto toPaymentDto(Payment payment);

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "paymentState", constant = "PENDING")
    @Mapping(target = "totalPayment", source = "totalPrice")
    @Mapping(target = "deliveryTotal", source = "deliveryPrice")
    @Mapping(target = "orderId", source = "orderId")
    Payment toPayment(OrderDto orderDto);

    @AfterMapping
    default void fillFeeTotal(OrderDto orderDto, @MappingTarget Payment payment) {
        if (orderDto.getProductPrice() != null) {
            payment.setFeeTotal(orderDto.getProductPrice().multiply(AppConstants.NDS_RATE));
        } else {
            payment.setFeeTotal(java.math.BigDecimal.ZERO);
        }
    }
}
