package io.refactor.ordersservice;

import io.refactor.ordersservice.models.response.ExceptionResponse;
import io.refactor.ordersservice.models.response.OrderResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponse handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletResponse response) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        response.setStatus(422);
        return new ExceptionResponse(errors, "Validation error", 422);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public OrderResponse handleEmptyResultDataAccessException(HttpServletResponse response) {
        response.setStatus(404);

        return new OrderResponse(
                null,
                "Order not found",
                404
        );
    }

    @ExceptionHandler(ParseException.class)
    public ExceptionResponse handleParseException(ParseException ex, HttpServletResponse response) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        response.setStatus(422);
        return new ExceptionResponse(errors, "Validation error", 422);
    }
}