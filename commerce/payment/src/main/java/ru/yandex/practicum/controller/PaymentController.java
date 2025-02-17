package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentClient {

    private final PaymentService service;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        return service.createPayment(orderDto);
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        return service.calculateTotalCost(orderDto);
    }

    @Override
    public void successPayment(UUID orderId) {
        service.successPayment(orderId);
    }

    @Override
    public Double calculateProductCost(OrderDto orderDto) {
        return service.calculateProductCost(orderDto);
    }

    @Override
    public void failedPayment(UUID orderId) {
        service.failedPayment(orderId);
    }
}