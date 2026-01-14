package ru.practicum.order.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interaction.api.feign.contract.DeliveryContract;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient extends DeliveryContract {
}
