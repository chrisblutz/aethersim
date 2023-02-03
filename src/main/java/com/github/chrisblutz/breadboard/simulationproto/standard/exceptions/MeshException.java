package com.github.chrisblutz.breadboard.simulationproto.standard.exceptions;

import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshConnector;

public class MeshException extends RuntimeException {

    public MeshException(String message) {
        super(message);
    }

    public MeshException(String message, Throwable cause) {
        super(message, cause);
    }
}
