package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(schema = "warehouse", name = "product_warehouse")
@Data
public class ProductWarehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;
    @Column
    private UUID productId;
    @Column
    private Boolean fragile;
    @Column
    private Double weight;
    @Embedded
    private Dimension dimension;
    @Column
    private int quantity;
}
