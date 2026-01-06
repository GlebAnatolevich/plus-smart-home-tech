package ru.practicum.interaction.api.exception;

public class OrderOperationFailedException extends RuntimeException {
    public OrderOperationFailedException(String message) {
        super(message);
    }
}
