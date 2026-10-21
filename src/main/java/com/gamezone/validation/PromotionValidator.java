package com.gamezone.validation;

/**
 * Utility class providing validation methods for promotion inputs.
 */
public class PromotionValidator {

    private PromotionValidator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates that the target category of a category discount is VIDEOGAME,
     * CONSOLE or ACCESSORY (case-insensitive).
     *
     * @param targetCategory the category entered for the discount
     * @throws IllegalArgumentException if the category is null or not one of the accepted values
     */
    public static void validateTargetCategory(String targetCategory) {
        if (!"VIDEOGAME".equalsIgnoreCase(targetCategory)
                && !"CONSOLE".equalsIgnoreCase(targetCategory)
                && !"ACCESSORY".equalsIgnoreCase(targetCategory)) {
            throw new IllegalArgumentException("La categor\u00EDa objetivo debe ser VIDEOGAME, CONSOLE o ACCESSORY.");
        }
    }
}