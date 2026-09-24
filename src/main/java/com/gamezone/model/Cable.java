package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a cable accessory.
 * Extends Accessory to include cable-specific attributes like length and connector type.
 */
public class Cable extends Accessory {

    private double length;
    private String connectorType;

    /**
     * Constructs a new Cable.
     */
    public Cable(List<String> compatibleConsoleIds, String id, String title, double price, int stock, double length, String connectorType) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.length = length;
        this.connectorType = connectorType;
    }

    /**
     * Gets the length of the cable.
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets the length of the cable.
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Gets the connector type of the cable.
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * Sets the connector type of the cable.
     */
    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    /**
     * Returns a detailed description of the cable.
     */
    @Override
    public String getFullDescription() {
        return String.format("Cable: [ID: %s, Title: %s, Price: $%.2f, Stock: %d, Length: %.2fm, Compatible Consoles: %s]",
                getId(), getTitle(), getPrice(), getStock(), length, getCompatibleConsoleIds());
    }
    
    public String getCategory(){
        return "Cable";
    }
}