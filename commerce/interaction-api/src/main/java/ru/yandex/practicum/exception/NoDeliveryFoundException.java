package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class NoDeliveryFoundException extends ResponseStatusException {
    public NoDeliveryFoundException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}