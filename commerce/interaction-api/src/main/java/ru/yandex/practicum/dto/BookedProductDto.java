package ru.yandex.practicum.dto;

import lombok.Data;

@Data
public class BookedProductDto {
    private double deliveryWeight;
    private double deliveryVolume;
    private boolean fragile;
}
