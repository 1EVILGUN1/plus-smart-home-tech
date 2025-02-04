package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(schema = "shopping-cart", name = "booked-product")
@Data
public class BookedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double deliveryWeight;
    @Column
    private Double deliveryVolume;
    @Column
    private Boolean fragile;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private ShoppingCart cart;
}
