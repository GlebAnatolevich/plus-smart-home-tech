package ru.practicum.interaction.api.exception;

public class DeliveryOperationFailedException extends RuntimeException {
    public DeliveryOperationFailedException(String message) {
        super(message);
    }
}
