package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.enums.ShoppingCartStatus;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.ShoppingCartNotFoundException;
import ru.yandex.practicum.mapper.BookedProductMapper;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.BookedProduct;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.BookedProductRepository;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final BookedProductRepository bookedProductRepository;
    private final WarehouseClient client;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookedProductMapper bookedProductMapper;

    public ShoppingCartDto addProducts(Map<UUID, Integer> products, String userName) {
        log.info("Добавление товаров в корзину для пользователя: {}", userName);
        ShoppingCart cart = getOrCreateActiveCart(userName);

        products.forEach((productId, quantity) -> {
            log.debug("Добавление товара: {} с количеством: {} в корзину", productId, quantity);
            cart.getProducts().put(productId, quantity);
        });

        ShoppingCartDto cartDto = shoppingCartMapper.shoppingCartToDto(cart);
        log.debug("Проверка доступности товаров на складе");
        client.checkQuantity(cartDto);

        ShoppingCart savedCart = repository.save(cart);
        log.info("Всего {} товаров в корзине пользователя: {}", savedCart.getProducts().size(), userName);

        return shoppingCartMapper.shoppingCartToDto(savedCart);
    }

    public ShoppingCartDto getShoppingCart(String userName) {
        log.info("Получение корзины пользователя: {}", userName);
        return getActiveCart(userName)
                .map(shoppingCartMapper::shoppingCartToDto)
                .orElse(null);
    }

    public void deactivateShoppingCart(String userName) {
        log.info("Деактивация корзины пользователя: {}", userName);
        ShoppingCart cart = getActiveCartOrThrow(userName);

        cart.setStatus(ShoppingCartStatus.DEACTIVATE);
        repository.save(cart);
        log.debug("Корзина успешно деактивирована для пользователя: {}", userName);
    }

    public ShoppingCartDto removeProducts(String userName, Map<UUID, Integer> productsToRemove) {
        log.info("Удаление товаров из корзины пользователя: {}", userName);
        ShoppingCart cart = getActiveCartOrThrow(userName);

        productsToRemove.keySet().forEach(productId -> {
            log.debug("Удаление товара: {} из корзины", productId);
            cart.getProducts().remove(productId);
        });

        ShoppingCart updatedCart = repository.save(cart);
        log.info("Удалено {} товаров из корзины", productsToRemove.size());

        return shoppingCartMapper.shoppingCartToDto(updatedCart);
    }

    public ShoppingCartDto changeQuantityProducts(String userName, ChangeProductQuantityRequest request) {
        log.info("Изменение количества товара в корзине пользователя: {}", userName);
        ShoppingCart cart = getActiveCartOrThrow(userName);
        UUID productId = request.getProductId();

        if (!cart.getProducts().containsKey(productId)) {
            log.error("Товар: {} не найден в корзине", productId);
            throw new NoProductsInShoppingCartException("Товар не найден", new Throwable());
        }

        log.debug("Обновление количества товара: {} до {}", productId, request.getNewQuantity());
        cart.getProducts().put(productId, request.getNewQuantity());

        ShoppingCart updatedCart = repository.save(cart);
        return shoppingCartMapper.shoppingCartToDto(updatedCart);
    }

    public BookedProductDto bookedProducts(String userName) {
        log.info("Бронирование товаров для пользователя: {}", userName);
        ShoppingCart cart = getActiveCartOrThrow(userName);

        BookedProduct bookedProduct = createBookedProduct(cart);
        BookedProduct savedBooking = bookedProductRepository.save(bookedProduct);
        log.info("Товары успешно забронированы. ID бронирования: {}", savedBooking.getId());

        return bookedProductMapper.bookedProductsToDto(savedBooking);
    }

    private ShoppingCart getOrCreateActiveCart(String userName) {
        return getActiveCart(userName).orElseGet(() -> {
            log.debug("Создание новой активной корзины для пользователя: {}", userName);
            ShoppingCart newCart = new ShoppingCart();
            newCart.setStatus(ShoppingCartStatus.ACTIVE);
            newCart.setUserName(userName);
            return repository.save(newCart);
        });
    }

    private Optional<ShoppingCart> getActiveCart(String userName) {
        return repository.findByUserNameAndStatus(userName, ShoppingCartStatus.ACTIVE);
    }

    private ShoppingCart getActiveCartOrThrow(String userName) {
        return getActiveCart(userName).orElseThrow(() ->
                new ShoppingCartNotFoundException("Корзина не найдена"));
    }

    private BookedProduct createBookedProduct(ShoppingCart cart) {
        BookedProduct bookedProduct = new BookedProduct();
        bookedProduct.setCart(cart);
        bookedProduct.setFragile(true);
        bookedProduct.setDeliveryVolume(0.0);
        bookedProduct.setDeliveryWeight(0.0);
        log.debug("Создан объект бронирования товаров для корзины: {}", cart.getId());
        return bookedProduct;
    }
}
