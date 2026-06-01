package com.myproject.clinic.exception;

public class ChatSessionPersistenceException extends RuntimeException {

    public ChatSessionPersistenceException(String message) {
        super(message);
    }

    public ChatSessionPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
