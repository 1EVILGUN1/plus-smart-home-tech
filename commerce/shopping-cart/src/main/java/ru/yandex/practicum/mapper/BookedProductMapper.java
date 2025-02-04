package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.model.BookedProduct;

@Mapper
public interface BookedProductMapper {
    BookedProductMapper INSTANCE = Mappers.getMapper(BookedProductMapper.class);

    BookedProductDto bookedProductsToDto(BookedProduct bookedProduct);
}
