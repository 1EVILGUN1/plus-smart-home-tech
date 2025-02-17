package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class NoPaymentFoundException extends ResponseStatusException {
    public NoPaymentFoundException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}