package ru.yandex.practicum.exception;

public class NoProductsInShoppingCartException extends RuntimeException {
    public NoProductsInShoppingCartException(String message, Throwable cause) {
        super(message, cause);
    }
}
