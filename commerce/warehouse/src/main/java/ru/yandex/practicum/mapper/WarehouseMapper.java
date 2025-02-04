package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.model.ProductWarehouse;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@Mapper
public interface WarehouseMapper {
    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    ProductWarehouse newProductInWarehouseRequestToProductWarehouse(NewProductInWarehouseRequest request);
}
