package ru.yandex.practicum.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {
    private UUID productId;
    private Integer newQuantity;
}
