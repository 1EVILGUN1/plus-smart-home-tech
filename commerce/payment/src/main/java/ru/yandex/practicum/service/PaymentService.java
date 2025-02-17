package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentStatus;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final OrderClient orderClient;

    public PaymentDto createPayment(OrderDto orderDto) {
        Payment payment = new Payment();

        payment.setStatus(PaymentStatus.PENDING);
        return PaymentMapper.INSTANCE.paymentToDto(repository.save(payment));
    }

    public Double calculateTotalCost(OrderDto orderDto) {
        Optional<Payment> payment = repository.findById(orderDto.getPaymentId());
        if (payment.isEmpty()) {
            throw new NoPaymentFoundException(HttpStatus.NOT_FOUND, "Оплата не найдена");
        }

        payment.get().setDeliveryTotal(50d);
        payment.get().setFeeTotal(orderDto.getProductPrice() / 10);
        payment.get().setTotalPayment(orderDto.getProductPrice() +
                                      payment.get().getDeliveryTotal() +
                                      payment.get().getFeeTotal());
        PaymentMapper.INSTANCE.paymentToDto(repository.save(payment.get()));
        return payment.get().getTotalPayment();
    }

    public void successPayment(UUID orderId) {
        Optional<Payment> payment = repository.findByOrderId(orderId);
        if (payment.isEmpty()) {
            throw new NoPaymentFoundException(HttpStatus.NOT_FOUND, "Оплата не найдена");
        }

        payment.get().setStatus(PaymentStatus.SUCCESS);
        repository.save(payment.get());
        orderClient.paymentOrder(orderId);
    }

    public Double calculateProductCost(OrderDto orderDto) {
        Optional<Payment> payment = repository.findById(orderDto.getPaymentId());
        if (payment.isEmpty()) {
            throw new NoPaymentFoundException(HttpStatus.NOT_FOUND, "Оплата не найдена");
        }

        return orderDto.getProductPrice() + orderDto.getProductPrice() / 10;
    }

    public void failedPayment(UUID orderId) {
        Optional<Payment> payment = repository.findByOrderId(orderId);
        if (payment.isEmpty()) {
            throw new NoPaymentFoundException(HttpStatus.NOT_FOUND, "Оплата не найдена");
        }

        payment.get().setStatus(PaymentStatus.FAILED);
        repository.save(payment.get());
        orderClient.failedPaymentOrder(orderId);
    }
}