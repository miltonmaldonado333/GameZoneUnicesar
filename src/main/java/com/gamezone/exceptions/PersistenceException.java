package com.gamezone.exceptions;

/**
 * Exception thrown when an error occurs during file or storage I/O operations.
 */
public class PersistenceException extends GameZoneException {

    /**
     * Constructs a PersistenceException with a message and the underlying root cause.
     * 
     * @param message Contextual error message in Spanish.
     * @param cause Original cause of the persistence failure.
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, "PERSISTENCE_ERROR");
        initCause(cause);
    }
}