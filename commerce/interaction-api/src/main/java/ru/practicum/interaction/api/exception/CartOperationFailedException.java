package ru.practicum.interaction.api.exception;

public class CartOperationFailedException extends RuntimeException {
    public CartOperationFailedException(String message) {
        super(message);
    }
}
