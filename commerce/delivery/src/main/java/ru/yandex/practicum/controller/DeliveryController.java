package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {

    private final DeliveryService service;

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        return service.createDelivery(deliveryDto);
    }

    @Override
    public void successDelivery(UUID orderId) {
        service.successDelivery(orderId);
    }

    @Override
    public void pickDelivery(UUID orderId) {
        service.pickDelivery(orderId);
    }

    @Override
    public void failDelivery(UUID orderId) {
        service.failDelivery(orderId);
    }

    @Override
    public Double costDelivery(OrderDto orderDto) {
        return service.costDelivery(orderDto);
    }
}