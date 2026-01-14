package ru.practicum.order.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interaction.api.feign.contract.PaymentContract;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient extends PaymentContract {
}
