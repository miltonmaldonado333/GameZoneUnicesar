package com.gamezone.model;

import java.util.List;

/**
 * Model class representing a memory card accessory for gaming consoles.
 * Extends {@link Accessory} with capacity and storage type specifications.
 */
public class Memory extends Accessory {

    private int capacityGB;
    private String storageType;

    public Memory() {
    }

    public Memory(int capacityGB, String storageType, String id, String title, double price, int stock, List<String> compatibleConsoleIds) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.capacityGB = capacityGB;
        this.storageType = storageType;
    }

    public int getCapacityGB() {
        return capacityGB;
    }

    public void setCapacityGB(int capacityGB) {
        this.capacityGB = capacityGB;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    @Override
    public String getCategory() {
        return "Memory";
    }

    @Override
    public String getFullDescription() {
        return String.format("Memory: [ID: %s, Title: %s, Price: $%.2f, Stock: %d, Capacity: %d GB, Type: %s, Compatible Consoles: %s]",
                getId(), getTitle(), getPrice(), getStock(), capacityGB, storageType, getCompatibleConsoleIds());
    }
}