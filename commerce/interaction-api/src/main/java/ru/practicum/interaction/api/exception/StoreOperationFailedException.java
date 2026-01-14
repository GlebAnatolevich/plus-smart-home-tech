package ru.practicum.interaction.api.exception;

public class StoreOperationFailedException extends RuntimeException {
    public StoreOperationFailedException(String message) {
        super(message);
    }
}
