package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.model.Delivery;

@Mapper
public interface DeliveryMapper {
    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    Delivery dtoToDelivery(DeliveryDto dto);

    DeliveryDto DeliveryToDto(Delivery delivery);
}