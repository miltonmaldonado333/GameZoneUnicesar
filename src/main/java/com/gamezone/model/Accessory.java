package com.gamezone.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Abstract class representing a generic accessory in the inventory.
 */
public abstract class Accessory extends Product {

    private List<String> compatibleConsoleIds;

    /**
     * Constructs a new Accessory.
     */
    public Accessory(List<String> compatibleConsoleIds, String id, String title, double price, int stock) {
        super(id, title, price, stock);
        // Evitamos errores si la lista llega nula
        this.compatibleConsoleIds = (compatibleConsoleIds != null) ? compatibleConsoleIds : new ArrayList<>();
    }
    
    /**
     * Gets the list of compatible console IDs.
     *
     * @return List of strings representing console IDs.
     */
    public List<String> getCompatibleConsoleIds() {
        return compatibleConsoleIds;
    }
    
    /**
     * Adds a specific console ID to the compatibility list.
     *
     * @param consoleId The ID of the compatible console to add.
     */
    public void addCompatibleConsoleId(String consoleId) {
        if (consoleId != null && !consoleId.trim().isEmpty()) {
            this.compatibleConsoleIds.add(consoleId);
        }
    }
     
    /**
     * Returns a detailed description of the accessory.
     *
     * @return The formatted description string.
     */
    public abstract String getDescription();
    
}