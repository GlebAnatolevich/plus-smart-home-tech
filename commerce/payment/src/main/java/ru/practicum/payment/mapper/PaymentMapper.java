package ru.practicum.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
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
    @Mapping(
            target = "feeTotal",
            // -- если цена не null, умножаем ее на NDS_RATE, иначе ставим 0
            expression = "java(orderDto.getProductPrice() != null " +
                         "? orderDto.getProductPrice().multiply(AppConstants.NDS_RATE) : java.math.BigDecimal.ZERO)"
    )
    Payment toPayment(OrderDto orderDto);
}
