package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.model.Payment;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    PaymentDto paymentToDto(Payment payment);
}