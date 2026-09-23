package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a gaming controller.
 * Extends Accessory to include controller-specific attributes like connection type.
 */
public class Controller extends Accessory {

    private String connectionType;

    /**
     * Constructs a new Controller.
     */
    public Controller(List<String> compatibleConsoleIds, String id, String title, double price, int stock, String connectionType) {
        // Llama al constructor de Accessory respetando su orden de parámetros
        super(compatibleConsoleIds, id, title, price, stock);
        this.connectionType = connectionType;
    }

    /**
     * Gets the connection type of the controller.
     *
     * @return The connection type (e.g., wireless, wired).
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the connection type of the controller.
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    /**
     * Returns a detailed description of the controller.
     *
     * @return The formatted description string incorporating the connection type.
     */
    @Override
    public String getFullDescription() {
        return "Controller (ID: " + getId() + ") - Title: " + getTitle() + " - Connection Type: " + connectionType;
    }
}