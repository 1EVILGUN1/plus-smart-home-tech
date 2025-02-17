package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.model.Address;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    Address dtoToAddress(AddressDto dto);

    AddressDto addressToDto(Address address);
}