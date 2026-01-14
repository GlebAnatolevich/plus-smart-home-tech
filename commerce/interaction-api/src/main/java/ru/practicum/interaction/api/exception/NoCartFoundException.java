package ru.practicum.interaction.api.exception;

public class NoCartFoundException extends RuntimeException {
    public NoCartFoundException(String message) {
        super(message);
    }
}
