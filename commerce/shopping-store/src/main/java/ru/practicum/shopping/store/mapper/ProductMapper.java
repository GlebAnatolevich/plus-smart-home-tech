package ru.practicum.shopping.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.interaction.api.dto.store.ProductDto;
import ru.practicum.shopping.store.model.Product;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product entity);

    default Page<ProductDto> toDtoPage(Page<Product> entityPage) {
        List<ProductDto> dtos = entityPage.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Mapping(target = "productId", ignore = true)
    void updateFromDto(ProductDto dto, @MappingTarget Product entity);

    List<ProductDto> toDtoList(List<Product> entities);
}
