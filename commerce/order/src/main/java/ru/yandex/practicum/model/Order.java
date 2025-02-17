package ru.yandex.practicum.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(schema = "order_sch", name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID orderId;

    @Column
    private UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(name = "order-products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "count")
    private Map<UUID, Integer> products;

    @Column
    private UUID paymentId;
    @Column
    private UUID deliveryId;
    @Column
    private OrderState state;
    @Column
    private Double deliveryWeight;
    @Column
    private Double deliveryVolume;
    @Column
    private Boolean fragile;
    @Column
    private Double totalPrice;
    @Column
    private Double deliveryPrice;
    @Column
    private Double productPrice;
    @Column
    private String userName;

    @Embedded
    private Address address;

}