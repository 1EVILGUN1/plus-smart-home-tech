package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.ProductState;
import ru.yandex.practicum.dto.enums.QuantityState;

import java.util.UUID;

@Entity
@Table(schema = "shopping-store", name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;
    @Column
    private String productName;
    @Column
    private String description;
    @Column(nullable = true)
    private String imageSrc;
    @Column
    private QuantityState quantityState;
    @Column
    private ProductState productState;
    @Column
    private ProductCategory productCategory;
    @Min(value = 1)
    @Column
    private double price;
}
