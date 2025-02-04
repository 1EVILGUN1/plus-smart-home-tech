package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.BookedProduct;

@Repository
public interface BookedProductRepository extends JpaRepository<BookedProduct, Long> {
}
