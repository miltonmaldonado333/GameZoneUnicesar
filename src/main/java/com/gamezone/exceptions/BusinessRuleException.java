package com.gamezone.exceptions;

/**
 * Exception thrown when a business rule or domain invariant is violated.
 */
public class BusinessRuleException extends GameZoneException {

    /**
     * Constructs a BusinessRuleException with the specified detailed message.
     * 
     * @param message Explanation of the violated business rule in Spanish.
     */
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }
}