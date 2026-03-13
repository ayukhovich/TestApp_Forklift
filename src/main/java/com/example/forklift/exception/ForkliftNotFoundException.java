package com.example.forklift.exception;

/**
 * Исключение для случая, когда погрузчик не найден
 */
public class ForkliftNotFoundException extends RuntimeException {

    public ForkliftNotFoundException(String message) {
        super(message);
    }
}
