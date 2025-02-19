package ru.yandex.practicum.dto;

import lombok.Data;
import ru.yandex.practicum.dto.enums.DeliveryState;

import java.util.UUID;

@Data
public class DeliveryDto {
    private UUID deliveryId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private UUID orderId;
    private DeliveryState deliveryState;
}