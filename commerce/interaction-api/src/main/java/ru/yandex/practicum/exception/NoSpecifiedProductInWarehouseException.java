package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class NoSpecifiedProductInWarehouseException extends ResponseStatusException {
    public NoSpecifiedProductInWarehouseException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
