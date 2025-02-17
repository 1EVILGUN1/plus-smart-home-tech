package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class NotEnoughInfoInOrderToCalculateException extends ResponseStatusException {
    public NotEnoughInfoInOrderToCalculateException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}