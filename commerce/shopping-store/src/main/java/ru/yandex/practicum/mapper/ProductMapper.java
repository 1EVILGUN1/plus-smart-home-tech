package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.Product;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product dtoToProduct(ProductDto dto);

    ProductDto productToDto(Product product);

    List<ProductDto> productsToProductDtoItems(List<Product> products);
}
