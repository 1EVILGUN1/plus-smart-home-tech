package ru.yandex.practicum.exception;

public class ProductInShoppingCartLowQuantityInWarehouse extends Exception {
    public ProductInShoppingCartLowQuantityInWarehouse(String message) {
        super(message);
    }
}
