
package com.gamezone.model;

import java.util.List;

public class Cable extends Accessory {
    private double length;
    private String connectorType;

    public Cable(double length, String connectorType, List<String> compatibleConsoleIds, String id, String title, double price, int stock) {
        super(compatibleConsoleIds, id, title, price, stock);
        this.length = length;
        this.connectorType = connectorType;
    }

   

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }
    @Override
    public String getFullDescription() {
        return "Cable (ID: " + getId() + ") - Title: " + getTitle() + " - Length: " + length + "m - Connector: " + connectorType;
    }
    
}
