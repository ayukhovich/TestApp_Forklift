package com.example.forklift.exception;

/**
 * Исключение для случая, когда невозможно удалить погрузчик из-за наличия простоев
 */
public class ForkliftHasDowntimesException extends RuntimeException {

    public ForkliftHasDowntimesException(String message) {
        super(message);
    }
}
