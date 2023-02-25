package com.aethersim.designs.exceptions;

public class DesignException extends RuntimeException {

    public DesignException(String message) {
        super(message);
    }

    public DesignException(String message, Throwable cause) {
        super(message, cause);
    }
}
