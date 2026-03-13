package com.example.forklift.exception;

/**
 * Исключение для случая, когда простой не найден
 */
public class DowntimeNotFoundException extends RuntimeException {

    public DowntimeNotFoundException(String message) {
        super(message);
    }
}
