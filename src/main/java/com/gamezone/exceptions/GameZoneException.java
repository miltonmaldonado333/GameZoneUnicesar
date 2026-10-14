package com.gamezone.exceptions;

/**
 * Root abstract exception for the GameZone application.
 * Extends RuntimeException to represent unchecked business and domain errors.
 */
public abstract class GameZoneException extends RuntimeException {
    
    private final String errorCode;

    /**
     * Constructor with a descriptive message.
     * 
     * @param message Detail message describing the error.
     */
    public GameZoneException(String message) {
        super(message);
        this.errorCode = "GZ-GENERIC";
    }

    /**
     * Constructor with a descriptive message and an explicit error code.
     * 
     * @param message Detail message describing the error.
     * @param errorCode Unique identifier for the error category.
     */
    public GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Gets the specific error code.
     * 
     * @return The error code string.
     */
    public String getErrorCode() {
        return errorCode;
    }
}