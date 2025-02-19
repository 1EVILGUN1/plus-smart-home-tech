package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentStatus;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final OrderClient orderClient;
    private final PaymentMapper paymentMapper;

    public PaymentDto createPayment(OrderDto orderDto) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        return paymentMapper.paymentToDto(repository.save(payment));
    }

    public Double calculateTotalCost(OrderDto orderDto) {
        Payment payment = findPaymentById(orderDto.getPaymentId());

        payment.setDeliveryTotal(50d);
        payment.setFeeTotal(orderDto.getProductPrice() / 10);
        payment.setTotalPayment(orderDto.getProductPrice() + payment.getDeliveryTotal() + payment.getFeeTotal());

        repository.save(payment);
        return payment.getTotalPayment();
    }

    public void successPayment(UUID orderId) {
        updatePaymentStatus(orderId, PaymentStatus.SUCCESS);
        orderClient.paymentOrder(orderId);
    }

    public Double calculateProductCost(OrderDto orderDto) {
        findPaymentById(orderDto.getPaymentId()); // Просто проверяем наличие оплаты
        return orderDto.getProductPrice() * 1.1;
    }

    public void failedPayment(UUID orderId) {
        updatePaymentStatus(orderId, PaymentStatus.FAILED);
        orderClient.failedPaymentOrder(orderId);
    }

    private Payment findPaymentById(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Оплата не найдена"));
    }

    private void updatePaymentStatus(UUID orderId, PaymentStatus status) {
        Payment payment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new NoPaymentFoundException("Оплата не найдена"));

        payment.setStatus(status);
        repository.save(payment);
    }
}