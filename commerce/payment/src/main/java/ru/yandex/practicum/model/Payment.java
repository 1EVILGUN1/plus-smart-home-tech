package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(schema = "payment", name = "payment")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID paymentId;
    @Column
    private Double totalPayment;
    @Column
    private Double deliveryTotal;
    @Column
    private Double feeTotal;
    @Column
    private PaymentStatus status;
    @Column
    private UUID orderId;
}