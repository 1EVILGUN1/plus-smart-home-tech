package ru.yandex.practicum.exception;

public class ProductInShoppingCartNotInWarehouse extends RuntimeException {
    public ProductInShoppingCartNotInWarehouse(String message, Throwable cause) {
        super(message, cause);
    }
}
