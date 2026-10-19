package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a sale transaction in the GameZone system, supporting products,
 * accessories, applied promotion discounts, extended warranties, return eligibility,
 * and detailed receipt generation.
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

    // Warranty attributes (Requirement 4 & Integration A3)
    private double extendedWarrantyCost;

    /**
     * Default constructor for Sale.
     */
    public Sale() {
    }

    /**
     * Parameterized constructor that initializes the sale transaction and computes the total price.
     *
     * @param id the unique identifier for the sale
     * @param date the transaction date string (YYYY-MM-DD)
     * @param client the purchasing client entity
     * @param seller the attending seller employee entity
     * @param products the list of products and accessories included in the sale
     */
    public Sale(int id, String date, Client client, Seller seller, List<Product> products) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.seller = seller;
        this.products = products;
        this.discountAmount = 0.0;
        this.extendedWarrantyCost = 0.0;
        this.total = calculateTotal();
    }

    /**
     * Calculates the subtotal of the sale by summing up the individual prices of all items.
     *
     * @return the calculated subtotal before discounts and warranties
     */
    public double getSubtotal() {
        double sum = 0.0;
        if (products != null) {
            for (Product product : products) {
                if (product != null) {
                    sum += product.getPrice();
                }
            }
        }
        return sum;
    }

    /**
     * Calculates and updates the total price based on subtotal, applied promotion discount, 
     * and extended warranty cost.
     *
     * @return the final calculated total
     */
    public double calculateTotal() {
        this.total = getSubtotal() - this.discountAmount + this.extendedWarrantyCost;
        return this.total;
    }

    /**
     * Generates a detailed text receipt displaying transaction info, purchased items,
     * subtotal, applied promotion discount, extended warranty costs, and final total.
     *
     * @return a formatted receipt string for user display
     */
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("            GAMEZONE UNICESAR             \n");
        sb.append("            RECIBO DE VENTA               \n");
        sb.append("=========================================\n");
        sb.append("ID Venta: ").append(id).append("\n");
        sb.append("Fecha   : ").append(date).append("\n");
        if (client != null) {
            sb.append("Cliente : ").append(client.getName()).append(" (").append(client.getIdentification()).append(")\n");
        }
        if (seller != null) {
            sb.append("Vendedor: ").append(seller.getName()).append(" (").append(seller.getEmployeeCode()).append(")\n");
        }
        sb.append("-----------------------------------------\n");
        sb.append("ÍTEMS COMPRADOS:\n");

        if (products != null) {
            for (Product item : products) {
                if (item != null) {
                    sb.append(String.format("- %s ($%.2f)\n", item.getTitle(), item.getPrice()));
                }
            }
        }

        double subtotal = getSubtotal();
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Subtotal                : $%.2f\n", subtotal));

        if (appliedPromotionName != null && !appliedPromotionName.isEmpty() && discountAmount > 0) {
            sb.append(String.format("Descuento (%s) : -$%.2f\n", appliedPromotionName, discountAmount));
        } else {
            sb.append("Descuento               : -$0.00\n");
        }

        sb.append(String.format("Garantías Extendidas    : +$%.2f\n", extendedWarrantyCost));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("TOTAL FINAL             : $%.2f\n", total));
        sb.append("=========================================\n");

        return sb.toString();
    }

    /**
     * Alias method returning the list of items to maintain compatibility with services expecting getItems().
     *
     * @return the list of products and accessories included in this sale
     */
    public List<Product> getItems() {
        return products;
    }

    /**
     * Evaluates return policy eligibility against an explicit reference date.
     * 
     * GRASP Protected Variations (V-14): Decouples the 30-day policy rule from 
     * direct system clock reliance.
     *
     * @param evaluationDate the date to compare against the sale date
     * @return true if the sale is within 30 days of evaluationDate; false otherwise
     */
    public boolean canBeReturned(java.time.LocalDate evaluationDate) {
        if (this.date == null || this.date.trim().isEmpty() || evaluationDate == null) {
            return false;
        }
        
        java.time.LocalDate saleDate = java.time.LocalDate.parse(this.date.trim());
        return !evaluationDate.isAfter(saleDate.plusDays(30));
    }

    /**
     * Checks whether the sale transaction is eligible for a return based on current system date.
     *
     * @return true if the sale date is within 30 days of the current date; false otherwise
     */
    public boolean canBeReturned() {
        return canBeReturned(java.time.LocalDate.now());
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

    public Client getClient() { 
        return client; 
    }

    public void setClient(Client client) { 
        this.client = client; 
    }

    public Seller getSeller() { 
        return seller; 
    }

    public void setSeller(Seller seller) { 
        this.seller = seller; 
    }

    public List<Product> getProducts() { 
        return products; 
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        this.total = calculateTotal(); 
    }

    public double getTotal() { 
        return total; 
    }

    public void setTotal(double total) { 
        this.total = total; 
    }

    public String getAppliedPromotionName() { 
        return appliedPromotionName; 
    }

    public void setAppliedPromotionName(String appliedPromotionName) { 
        this.appliedPromotionName = appliedPromotionName; 
    }

    public double getDiscountAmount() { 
        return discountAmount; 
    }

    public void setDiscountAmount(double discountAmount) { 
        this.discountAmount = discountAmount; 
    }

    public double getExtendedWarrantyCost() { 
        return extendedWarrantyCost; 
    }

    public void setExtendedWarrantyCost(double extendedWarrantyCost) { 
        this.extendedWarrantyCost = extendedWarrantyCost; 
    }
}