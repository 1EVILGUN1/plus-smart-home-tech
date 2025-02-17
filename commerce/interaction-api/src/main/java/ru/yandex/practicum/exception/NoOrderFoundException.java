package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class NoOrderFoundException extends ResponseStatusException {
    public NoOrderFoundException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}