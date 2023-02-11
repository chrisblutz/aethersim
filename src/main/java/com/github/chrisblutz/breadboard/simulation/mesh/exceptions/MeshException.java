package com.github.chrisblutz.breadboard.simulation.mesh.exceptions;

public class MeshException extends RuntimeException {

    public MeshException(String message) {
        super(message);
    }

    public MeshException(String message, Throwable cause) {
        super(message, cause);
    }
}
