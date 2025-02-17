package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.request.CreateNewOrderRequest;
import ru.yandex.practicum.request.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderClient {

    private final OrderService service;

    @Override
    public OrderDto getOrderByUser(String userName) {
        return service.getOrderByUser(userName);
    }

    @Override
    public OrderDto createOrder(String userName, CreateNewOrderRequest request) {
        return service.createOrder(userName, request);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        return service.returnOrder(request);
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) {
        return service.paymentOrder(orderId);
    }

    @Override
    public OrderDto failedPaymentOrder(UUID orderId) {
        return service.faildeDeliveryOrder(orderId);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        return service.deliveryOrder(orderId);
    }

    @Override
    public OrderDto faildeDeliveryOrder(UUID orderId) {
        return service.faildeDeliveryOrder(orderId);
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        return service.completedOrder(orderId);
    }

    @Override
    public OrderDto calculateTotalOrder(UUID orderId) {
        return service.calculateTotalOrder(orderId);
    }

    @Override
    public OrderDto calculateDeliveryOrder(UUID orderId) {
        return service.calculateDeliveryOrder(orderId);
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        return service.assemblyOrder(orderId);
    }

    @Override
    public OrderDto failedAssemblyOrder(UUID orderId) {
        return failedPaymentOrder(orderId);
    }
}