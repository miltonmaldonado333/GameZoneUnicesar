package com.gamezone.exceptions;

/**
 * Exception thrown when input data fails formatting, range, or validation checks.
 */
public class InvalidDataException extends GameZoneException {

    /**
     * Constructs an InvalidDataException with a formatted Spanish message.
     * 
     * @param fieldName Name of the invalid field.
     * @param issue Description of the validation issue.
     */
    public InvalidDataException(String fieldName, String issue) {
        super(String.format("El campo '%s' es inválido: %s", fieldName, issue), "INVALID_DATA");
    }
}