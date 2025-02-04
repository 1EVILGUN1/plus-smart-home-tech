package ru.yandex.practicum.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SetProductQuantityStateRequest {
    private UUID productId;
    private String quantityState;
}
