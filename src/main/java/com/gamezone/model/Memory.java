package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a memory accessory.
 * Extends Accessory to include memory-specific attributes like capacity and storage type.
 */
public class Memory extends Accessory {

    private int capacity;
    private String storageType;

    /**
     * Constructs a new Memory accessory..
     */
    public Memory(List<String> compatibleConsoleIds, String id, String title, double price, int stock, int capacity, String storageType) {
        super(compatibleConsoleIds, id, title, price, stock);
        this.capacity = capacity;
        this.storageType = storageType;
    }

    /**
     * Gets the capacity of the memory.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the capacity of the memory.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the storage type of the memory.
     */
    public String getStorageType() {
        return storageType;
    }

    /**
     * Sets the storage type of the memory.
     */
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    /**
     * Returns a detailed description of the memory accessory.
     */
    @Override
    public String getFullDescription() {
        return "Memory (ID: " + getId() + ") - Title: " + getTitle() + " - Capacity: " + capacity + "GB - Type: " + storageType;
    }
}