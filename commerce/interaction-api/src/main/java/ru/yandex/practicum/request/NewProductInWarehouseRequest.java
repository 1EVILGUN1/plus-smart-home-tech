package ru.yandex.practicum.request;

import lombok.Data;
import ru.yandex.practicum.dto.DimensionDto;

import java.util.UUID;

@Data
public class NewProductInWarehouseRequest {
    private UUID productId;
    private Boolean fragile;
    private DimensionDto dimension;
    private Double weight;
}
