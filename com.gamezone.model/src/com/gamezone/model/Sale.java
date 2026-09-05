package com.gamezone.model;

import java.util.List;

public class Sale {
    private int id;
    private String date;
    private String customerName;
    private String sellerName;
    private List<Double> productPrices; 
    private double total;

    public Sale(int id, String date, String customerName, String sellerName, List<Double> productPrices) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.sellerName = sellerName;
        this.productPrices = productPrices;
        this.total = calculateTotal();
    }

    /**
     * Calculates the total price by summing all product prices.
     * @return the calculated total
     */
    public double calculateTotal() {
        double sum = 0.0;
        if (productPrices != null) {
            for (double price : productPrices) {
                sum += price;
            }
        }
        this.total = sum;
        return this.total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<Double> getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(List<Double> productPrices) {
        this.productPrices = productPrices;
        this.total = calculateTotal(); 
    }

    public double getTotal() {
        return total;
    }
}