package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService service;

    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam String username, @RequestBody Map<UUID, Integer> dto) {
        log.info("Получен запрос на добавление товаров в корзину для пользователя: {}", username);
        log.debug("Товары для добавления: {}", dto);

        ShoppingCartDto result = service.addProducts(dto, username);

        log.info("Товары успешно добавлены в корзину для пользователя: {}", username);
        return result;
    }

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("Получен запрос на получение корзины пользователя: {}", username);

        ShoppingCartDto result = service.getShoppingCart(username);

        if (result == null) {
            log.warn("Корзина не найдена для пользователя: {}", username);
        } else {
            log.debug("Возвращаем корзину пользователя: {} с {} товарами", username,
                    result.getProducts() != null ? result.getProducts().size() : 0);
        }
        return result;
    }

    @DeleteMapping
    public void deleteShoppingCart(@RequestParam String username) {
        log.info("Получен запрос на удаление корзины пользователя: {}", username);

        service.deactivateShoppingCart(username);

        log.info("Корзина успешно деактивирована для пользователя: {}", username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username, @RequestBody Map<UUID, Integer> dto) {
        log.info("Получен запрос на удаление товаров из корзины пользователя: {}", username);
        log.debug("Товары для удаления: {}", dto);

        ShoppingCartDto result = service.removeProducts(username, dto);

        log.info("Товары успешно удалены из корзины пользователя: {}", username);
        return result;
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantityProducts(@RequestParam String username,
                                                  @RequestBody ChangeProductQuantityRequest request) {
        log.info("Получен запрос на изменение количества товара в корзине пользователя: {}", username);
        log.debug("Детали запроса на изменение: {}", request);

        ShoppingCartDto result = service.changeQuantityProducts(username, request);

        log.info("Количество товара успешно изменено для пользователя: {}", username);
        return result;
    }
}
