package ru.practicum.payment.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interaction.api.feign.contract.OrderContract;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderContract {
}
