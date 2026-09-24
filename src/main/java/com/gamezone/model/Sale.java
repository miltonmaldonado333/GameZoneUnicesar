package com.gamezone.model;

import java.util.List;

/**
 * Represents a sale transaction in the GameZone system.
 */
public class Sale {
    // Sale attributes
    private int id;
    private String date;
    private Client client;
    private Seller seller;
    private List<Product> products; 
    private double total;

    // Promotion / Discount attributes (Requirement 2)
    private String appliedPromotionName;
    private double discountAmount;

    // Default constructor
    public Sale() {
    }

    // Parameterized constructor that initializes the sale and computes the total
    public Sale(int id, String date, Client client, Seller seller, List<Product> products) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.seller = seller;
        this.products = products;
        this.total = calculateTotal();
    }

    // Calculates the total price by summing up all products in the sale (Subtotal)
    public double calculateTotal() {
        double sum = 0.0;
        if (products != null) {
            for (Product product : products) {
                sum += product.getPrice();
            }
        }
        this.total = sum - this.discountAmount;
        return this.total;
    }

    // Alias for calculateTotal / Subtotal
    public double getSubtotal() {
        double sum = 0.0;
        if (products != null) {
            for (Product product : products) {
                sum += product.getPrice();
            }
        }
        return sum;
    }

    // Alias to match service calls expecting getItems()
    public List<Product> getItems() {
        return products;
    }
   /**
     * Checks if the sale is eligible for a return based on the 30-day policy.
     * 
     * @return true if the current date is within 30 days of the sale date, false otherwise.
     */
    public boolean canBeReturned() {
        if (this.date == null || this.date.isEmpty()) {
            return false;
        }
        
        java.time.LocalDate saleDate = java.time.LocalDate.parse(this.date);
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        
        return !currentDate.isAfter(saleDate.plusDays(30));
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) {
        this.products = products;
        this.total = calculateTotal(); 
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // Getters and Setters for Promotions
    public String getAppliedPromotionName() { return appliedPromotionName; }
    public void setAppliedPromotionName(String appliedPromotionName) { 
        this.appliedPromotionName = appliedPromotionName; 
    }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { 
        this.discountAmount = discountAmount; 
    }
}