package com.aethersim.projects.io.exceptions;

public class ProjectIOException extends RuntimeException {

    public ProjectIOException(String message) {
        super(message);
    }

    public ProjectIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
