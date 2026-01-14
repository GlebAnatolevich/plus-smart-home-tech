package ru.practicum.delivery.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interaction.api.feign.contract.WarehouseContract;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends WarehouseContract {
}
