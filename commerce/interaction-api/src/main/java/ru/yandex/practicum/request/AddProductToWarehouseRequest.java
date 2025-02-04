package ru.yandex.practicum.request;

import lombok.Data;

import java.util.UUID;

@Data
public class AddProductToWarehouseRequest {
    private UUID productId;
    private Integer quantity;
}
