package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.enums.DeliveryState;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository repository;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryCostCalculator costCalculator;
    private final DeliveryStateService deliveryStateService;

    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        log.info("Создание доставки для заказа: {}", deliveryDto.getOrderId());

        Delivery delivery = deliveryMapper.dtoToDelivery(deliveryDto);
        Delivery savedDelivery = repository.save(delivery);

        log.info("Доставка успешно создана: {}", savedDelivery.getOrderId());
        return deliveryMapper.DeliveryToDto(savedDelivery);
    }

    public void successDelivery(UUID orderId) {
        log.info("Обновление статуса доставки для заказа {} → {}", orderId, DeliveryState.DELIVERED);
        deliveryStateService.updateDeliveryStatus(orderId, DeliveryState.DELIVERED);
    }

    public void pickDelivery(UUID orderId) {
        log.info("Обновление статуса доставки для заказа {} → {}", orderId, DeliveryState.IN_PROGRESS);
        deliveryStateService.updateDeliveryStatus(orderId, DeliveryState.IN_PROGRESS);
    }

    public void failDelivery(UUID orderId) {
        log.warn("Обновление статуса доставки для заказа {} → {}", orderId, DeliveryState.FAILED);
        deliveryStateService.updateDeliveryStatus(orderId, DeliveryState.FAILED);
    }

    public Double costDelivery(OrderDto orderDto) {
        log.info("Расчет стоимости доставки для заказа: {}", orderDto.getOrderId());
        Double cost = costCalculator.calculateCost(orderDto);
        log.info("Стоимость доставки для заказа {}: {} руб.", orderDto.getOrderId(), cost);
        return cost;
    }
}