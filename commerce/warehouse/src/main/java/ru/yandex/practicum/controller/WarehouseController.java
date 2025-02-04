package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService service;

    @PutMapping
    public void createProduct(@RequestBody NewProductInWarehouseRequest product) {
        log.info("Получен запрос на добавление продукта в склад: {}", product);
        service.createProduct(product);
        log.info("Продукт успешно добавлен в склад: {}", product.getProductId());
    }

    @PostMapping("/check")
    public BookedProductDto checkQuantity(@RequestBody ShoppingCartDto cart) {
        log.info("Получен запрос на проверку количества товаров в корзине: {}", cart);
        BookedProductDto result = service.checkQuantity(cart);
        log.info("Проверка завершена, итоговый результат: {}", result);
        return result;
    }

    @PostMapping("/add")
    public void addProduct(@RequestBody AddProductToWarehouseRequest product) {
        log.info("Получен запрос на добавление количества продукта в склад: {}", product);
        service.addProduct(product);
        log.info("Количество продукта успешно обновлено. ID продукта: {}, Добавлено: {}",
                product.getProductId(), product.getQuantity());
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("Получен запрос на получение адреса склада");
        AddressDto address = service.getAddress();
        log.info("Адрес склада: {}", address);
        return address;
    }
}
