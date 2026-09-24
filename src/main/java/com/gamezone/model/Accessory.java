package com.gamezone.model;

import java.util.List;

/**
 * Abstract class representing a generic console accessory.
 * Extends {@link Product} to support item compatibility.
 */
public abstract class Accessory extends Product {

    private List<String> compatibleConsoleIds;

    public Accessory() {
    }

    public Accessory(String id, String title, double price, int stock, List<String> compatibleConsoleIds) {
        super(id, title, price, stock);
        this.compatibleConsoleIds = compatibleConsoleIds;
    }

    public List<String> getCompatibleConsoleIds() {
        return compatibleConsoleIds;
    }

    public void setCompatibleConsoleIds(List<String> compatibleConsoleIds) {
        this.compatibleConsoleIds = compatibleConsoleIds;
    }

    /**
     * Returns the general category identifier for accessories.
     *
     * @return the category name "Accessory"
     */
    @Override
    public String getCategory() {
        return "Accessory";
    }
}