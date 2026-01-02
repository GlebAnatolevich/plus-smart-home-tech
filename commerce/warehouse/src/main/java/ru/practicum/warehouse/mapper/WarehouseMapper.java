package ru.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.practicum.warehouse.model.ProductStorage;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WarehouseMapper {
    @Mapping(target = "quantity", constant = "0L")
    ProductStorage toWarehouse(NewProductInWarehouseRequestDto requestDto);
}
