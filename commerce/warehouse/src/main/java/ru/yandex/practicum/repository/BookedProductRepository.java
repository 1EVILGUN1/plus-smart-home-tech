package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.BookedProduct;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookedProductRepository extends JpaRepository<BookedProduct, UUID> {
    Optional<BookedProduct> findByOrderId(UUID orderId);
}