package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ProductInShoppingCartLowQuantityInWarehouse extends ResponseStatusException {
    public ProductInShoppingCartLowQuantityInWarehouse(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
