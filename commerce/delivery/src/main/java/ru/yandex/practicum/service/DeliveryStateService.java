package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.request.ShippedToDeliveryRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryStateService {
    private final DeliveryRepository repository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public void updateDeliveryStatus(UUID orderId, DeliveryState state) {
        Delivery delivery = repository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена"));

        delivery.setDeliveryState(state);
        repository.save(delivery);

        switch (state) {
            case DELIVERED -> orderClient.completedOrder(orderId);
            case FAILED -> orderClient.faildeDeliveryOrder(orderId);
            case IN_PROGRESS -> {
                ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
                request.setOrderId(orderId);
                request.setDeliveryId(delivery.getDeliveryId());
                warehouseClient.shipped(request);
            }
        }
    }
}
