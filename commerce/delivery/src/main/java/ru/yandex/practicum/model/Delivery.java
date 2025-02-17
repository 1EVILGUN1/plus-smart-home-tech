package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.dto.enums.DeliveryState;

import java.util.UUID;

@Entity
@Table(schema = "delivery", name = "delivery")
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID deliveryId;

    @Embedded
    private Address fromAddress;
    @Embedded
    private Address toAddress;
    @Column
    private UUID orderId;
    @Column
    private DeliveryState deliveryState;
    @Column
    private Double deliveryWeight;
    @Column
    private Double deliveryVolume;
    @Column
    private Boolean fragile;

}