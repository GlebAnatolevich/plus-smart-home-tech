package ru.practicum.payment.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interaction.api.feign.contract.StoreContract;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient extends StoreContract {
}
