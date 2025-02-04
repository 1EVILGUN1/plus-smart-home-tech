package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.ProductWarehouse;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductWarehouseRepository extends JpaRepository<ProductWarehouse, UUID> {
    Optional<ProductWarehouse> findByProductId(UUID productId);
}
