package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery-client", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void successDelivery(UUID orderId);

    @PostMapping("/picked")
    void pickDelivery(UUID orderId);

    @PostMapping("/failed")
    void failDelivery(UUID orderId);

    @PostMapping("/cost")
    Double costDelivery(OrderDto orderDto);
}