package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.model.BookedProduct;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookedProductMapper {
    BookedProductDto bookedProductsToDto(BookedProduct bookedProduct);
}
