package com.gamezone.persistence;

import java.util.LinkedHashMap;
import java.util.Map;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Warranty;

/**
 * Keeps the text that identifies each warranty class in the warranties file, so
 * the repository writes the type of a warranty without testing its class. A new
 * warranty type is registered here and the repository does not change.
 */
public class WarrantyTypeRegistry {

    /** Stored type of a basic warranty. */
    public static final String BASIC = "BASIC";

    /** Stored type of an extended warranty. */
    public static final String EXTENDED = "EXTENDED";

    private final Map<Class<? extends Warranty>, String> types = new LinkedHashMap<>();

    /**
     * Registers the stored type of a warranty class.
     *
     * @param warrantyClass the warranty class
     * @param type          the text stored as the first field of the line
     * @return this registry, to chain registrations
     */
    public WarrantyTypeRegistry register(Class<? extends Warranty> warrantyClass, String type) {
        types.put(warrantyClass, type);
        return this;
    }

    /**
     * Creates a registry with the types of the basic and the extended warranty.
     *
     * @return a registry with the default types
     */
    public static WarrantyTypeRegistry withDefaults() {
        return new WarrantyTypeRegistry()
                .register(ExtendedWarranty.class, EXTENDED)
                .register(BasicWarranty.class, BASIC);
    }

    /**
     * Gets the stored type of a warranty, using the first registered class it is an instance of.
     *
     * @param warranty the warranty to write
     * @return the text stored as the first field of the line
     * @throws IllegalArgumentException if no type is registered for the class of the warranty
     */
    public String typeOf(Warranty warranty) {
        for (Map.Entry<Class<? extends Warranty>, String> entry : types.entrySet()) {
            if (entry.getKey().isInstance(warranty)) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException(
                "No stored type is registered for the warranty class " + warranty.getClass().getSimpleName());
    }
}