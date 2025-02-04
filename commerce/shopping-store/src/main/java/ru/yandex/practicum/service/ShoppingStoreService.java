package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.ProductState;
import ru.yandex.practicum.dto.enums.QuantityState;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ShoppingStoreRepository repository;

    public ProductDto create(ProductDto dto) {
        log.info("Создание нового продукта: {}", dto);

        Product product = ProductMapper.INSTANCE.dtoToProduct(dto);
        Product savedProduct = repository.save(product);

        log.info("Продукт успешно создан с ID: {}", savedProduct.getProductId());
        return ProductMapper.INSTANCE.productToDto(savedProduct);
    }

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Запрос списка продуктов категории: {}", category);

        Page<ProductDto> products = repository.findAllByProductCategory(category, pageable)
                .map(ProductMapper.INSTANCE::productToDto);

        log.info("Найдено {} продуктов в категории {}", products.getTotalElements(), category);
        return products;
    }

    public ProductDto getProduct(UUID productId) {
        log.info("Запрос продукта с ID: {}", productId);

        return repository.findById(productId)
                .map(product -> {
                    log.info("Продукт найден: {}", product);
                    return ProductMapper.INSTANCE.productToDto(product);
                })
                .orElseGet(() -> {
                    log.warn("Продукт с ID {} не найден", productId);
                    return null;
                });
    }

    public ProductDto update(ProductDto dto) {
        log.info("Обновление продукта с ID: {}", dto.getProductId());

        Product existingProduct = repository.findById(dto.getProductId())
                .orElseThrow(() -> {
                    log.error("Продукт с ID {} не найден", dto.getProductId());
                    return new NotFoundException(String.format("Продукт с ID %s не найден", dto.getProductId()));
                });

        Product updatedProduct = ProductMapper.INSTANCE.dtoToProduct(dto);
        updatedProduct.setProductId(existingProduct.getProductId()); // сохраняем ID
        Product savedProduct = repository.save(updatedProduct);

        log.info("Продукт с ID {} успешно обновлен", savedProduct.getProductId());
        return ProductMapper.INSTANCE.productToDto(savedProduct);
    }

    public Boolean remove(UUID productId) {
        log.info("Деактивация продукта с ID: {}", productId);

        Product product = repository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Продукт с ID {} не найден", productId);
                    return new NotFoundException(String.format("Продукт с ID %s не найден", productId));
                });

        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);

        log.info("Продукт с ID {} успешно деактивирован", productId);
        return true;
    }

    public Boolean setQuantityState(UUID productId, String state) {
        log.info("Изменение состояния количества товара для продукта с ID: {} на {}", productId, state);

        Product product = repository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Продукт с ID {} не найден", productId);
                    return new NotFoundException(String.format("Продукт с ID %s не найден", productId));
                });

        try {
            product.setQuantityState(QuantityState.valueOf(state));
            repository.save(product);

            log.info("Состояние количества товара успешно изменено для продукта с ID: {} на {}", productId, state);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при изменении состояния количества: недопустимое значение '{}'", state);
            throw new IllegalArgumentException(String.format("Недопустимое состояние количества: %s", state));
        }
    }
}
