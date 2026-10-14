package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;

import java.util.List;

/**
 * Utility class providing validation methods for Person entities and inputs.
 */
public class PersonValidator {

    private PersonValidator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates personal information and basic email structure.
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("Identificación", "no puede estar vacía");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nombre", "no puede estar vacío");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidDataException("Teléfono", "no puede estar vacío");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("Correo electrónico", "debe incluir '@' y un dominio válido (punto)");
        }
    }

    /**
     * Validates that a person instance exists.
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    /**
     * Ensures no duplicates exist in the collection for a given ID.
     */
    public static void validateUniquePerson(List<Person> existing, String newId) {
        if (existing == null || newId == null) return;
        
        boolean exists = existing.stream()
                .anyMatch(p -> p.getIdentification() != null && p.getIdentification().equalsIgnoreCase(newId.trim()));
        
        if (exists) {
            throw new BusinessRuleException(String.format("Ya existe una persona registrada con la identificación '%s'.", newId));
        }
    }
}