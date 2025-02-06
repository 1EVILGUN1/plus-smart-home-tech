package ru.yandex.practicum.exception;

public class NoSpecifiedProductInWarehouseException extends Exception {
    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }
}
