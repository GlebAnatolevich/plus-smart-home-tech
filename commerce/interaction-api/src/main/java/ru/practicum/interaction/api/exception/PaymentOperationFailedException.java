package ru.practicum.interaction.api.exception;

public class PaymentOperationFailedException extends RuntimeException {
    public PaymentOperationFailedException(String message) {
        super(message);
    }
}
