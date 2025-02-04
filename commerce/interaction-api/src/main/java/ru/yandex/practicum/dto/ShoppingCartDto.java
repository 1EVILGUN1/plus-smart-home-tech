package ru.yandex.practicum.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ShoppingCartDto {
    private UUID id;
    private Map<UUID, Integer> products;
}
