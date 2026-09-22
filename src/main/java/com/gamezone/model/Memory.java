/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.model;

import java.util.List;

/**
 *
 * @author HP
 */
public class Memory extends Accessory{
    private int capacity;
    private String storageType;
    

    public Memory(int capacity, String storageType, List<String> compatibleConsoleIds, String id, String title, double price, int stock) {
        super(compatibleConsoleIds, id, title, price, stock);
        this.capacity = capacity;
        this.storageType = storageType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }
    
    @Override
    public String getFullDescription() {
        return "Memory (ID: " + getId() + ") - Title: " + getTitle() + " - Capacity: " + capacity + "GB - Type: " + storageType;
    }
    
}
