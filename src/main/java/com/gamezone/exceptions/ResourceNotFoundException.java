package com.gamezone.exceptions;

/**
 * Exception thrown when a requested resource is not found in the system.
 */
public class ResourceNotFoundException extends GameZoneException {

    /**
     * Constructs a ResourceNotFoundException with a formatted Spanish message.
     * 
     * @param resourceType Name of the resource (e.g., "producto", "cliente").
     * @param resourceId Identifier of the missing resource.
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("El/La %s con identificador '%s' no fue encontrado.", resourceType, resourceId), "NOT_FOUND");
    }
}