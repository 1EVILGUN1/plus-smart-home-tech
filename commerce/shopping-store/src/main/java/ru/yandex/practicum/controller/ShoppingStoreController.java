package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ShoppingStoreService service;

    @PutMapping
    public ProductDto create(@RequestBody ProductDto productDto) {
        log.info("Получен запрос на создание нового продукта: {}", productDto);

        ProductDto result = service.create(productDto);

        log.info("Продукт успешно создан с ID: {}", result.getProductId());
        return result;
    }

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        log.info("Получен запрос на получение списка продуктов в категории: {}", category);

        Page<ProductDto> products = service.getProducts(category, pageable);

        log.info("Найдено {} продуктов в категории {}", products.getTotalElements(), category);
        return products;
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        log.info("Получен запрос на получение продукта с ID: {}", productId);

        ProductDto product = service.getProduct(productId);

        if (product == null) {
            log.warn("Продукт с ID {} не найден", productId);
        } else {
            log.info("Продукт с ID {} найден: {}", productId, product);
        }
        return product;
    }

    @PostMapping
    public ProductDto update(@RequestBody ProductDto productDto) {
        log.info("Получен запрос на обновление продукта с ID: {}", productDto.getProductId());

        ProductDto updatedProduct = service.update(productDto);

        log.info("Продукт с ID {} успешно обновлен", updatedProduct.getProductId());
        return updatedProduct;
    }

    @PostMapping("/removeProductFromStore")
    public Boolean remove(@RequestBody UUID productId) {
        log.info("Получен запрос на удаление продукта с ID: {}", productId);

        Boolean result = service.remove(productId);

        if (result) {
            log.info("Продукт с ID {} успешно деактивирован", productId);
        } else {
            log.warn("Не удалось деактивировать продукт с ID {}", productId);
        }
        return result;
    }

    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam UUID productId, @RequestParam String quantityState) {
        log.info("Получен запрос на изменение состояния количества товара для продукта с ID: {} на {}", productId, quantityState);

        Boolean result = service.setQuantityState(productId, quantityState);

        log.info("Состояние количества товара для продукта с ID {} успешно изменено на {}", productId, quantityState);
        return result;
    }
}
