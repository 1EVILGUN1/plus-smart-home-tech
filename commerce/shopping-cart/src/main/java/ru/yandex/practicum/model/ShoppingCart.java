package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.dto.enums.ShoppingCartStatus;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(schema = "shopping-cart", name = "cart")
@Data
public class ShoppingCart {
    @ElementCollection
    @CollectionTable(name = "cart-products", joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "count")
    public Map<UUID, Integer> products;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;
    @Column
    private String userName;
    @Column
    private ShoppingCartStatus status;


}
