package ru.practicum.interaction.api.exception;

public class WarehouseOperationFailedException extends RuntimeException {
    public WarehouseOperationFailedException(String message) {
        super(message);
    }
}
