package ru.yandex.practicum.request;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class AssemblyProductsForOrderRequest {
    private Map<UUID, Integer> products;
    private UUID orderId;
}
