package com.example.forklift.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обработка исключения ForkliftNotFoundException
     * @param exception исключение
     * @return ответ с кодом 404
     */
    @ExceptionHandler(ForkliftNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleForkliftNotFound(ForkliftNotFoundException exception) {
        logger.error("Погрузчик не найден: {}", exception.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Обработка исключения ForkliftHasDowntimesException
     * @param exception исключение
     * @return ответ с кодом 400
     */
    @ExceptionHandler(ForkliftHasDowntimesException.class)
    public ResponseEntity<Map<String, String>> handleForkliftHasDowntimes(ForkliftHasDowntimesException exception) {
        logger.error("Невозможно удалить погрузчик: {}", exception.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Обработка исключения DowntimeNotFoundException
     * @param exception исключение
     * @return ответ с кодом 404
     */
    @ExceptionHandler(DowntimeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDowntimeNotFound(DowntimeNotFoundException exception) {
        logger.error("Простой не найден: {}", exception.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Обработка всех остальных исключений
     * @param exception исключение
     * @return ответ с кодом 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception exception) {
        logger.error("Внутренняя ошибка сервера: {}", exception.getMessage(), exception);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Внутренняя ошибка сервера");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
