package com.gamezone.model;

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
                sum += product.getPrice();
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
        sb.append("           GAMEZONE UNICESAR             \n");
        sb.append("           RECIBO DE VENTA               \n");
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
                sb.append(String.format("- %s ($%.2f)\n", item.getTitle(), item.getPrice()));
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
     * Checks whether the sale transaction is eligible for a return based on the 30-day return policy.
     * 
     * @return true if the sale date is within 30 days of the current date; false otherwise
     */
    public boolean canBeReturned() {
        if (this.date == null || this.date.isEmpty()) {
            return false;
        }
        
        java.time.LocalDate saleDate = java.time.LocalDate.parse(this.date);
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        
        return !currentDate.isAfter(saleDate.plusDays(30));
    }

    /**
     * Gets the unique sale ID.
     *
     * @return the sale ID
     */
    public int getId() { 
        return id; 
    }

    /**
     * Sets the unique sale ID.
     *
     * @param id the sale ID to set
     */
    public void setId(int id) { 
        this.id = id; 
    }

    /**
     * Gets the sale date.
     *
     * @return the sale date string
     */
    public String getDate() { 
        return date; 
    }

    /**
     * Sets the sale date.
     *
     * @param date the sale date string to set
     */
    public void setDate(String date) { 
        this.date = date; 
    }

    /**
     * Gets the purchasing client.
     *
     * @return the client entity
     */
    public Client getClient() { 
        return client; 
    }

    /**
     * Sets the purchasing client.
     *
     * @param client the client entity to set
     */
    public void setClient(Client client) { 
        this.client = client; 
    }

    /**
     * Gets the attending seller.
     *
     * @return the seller entity
     */
    public Seller getSeller() { 
        return seller; 
    }

    /**
     * Sets the attending seller.
     *
     * @param seller the seller entity to set
     */
    public void setSeller(Seller seller) { 
        this.seller = seller; 
    }

    /**
     * Gets the list of products and accessories in the sale.
     *
     * @return the list of products
     */
    public List<Product> getProducts() { 
        return products; 
    }

    /**
     * Sets the list of products and recalculates total.
     *
     * @param products the list of products to set
     */
    public void setProducts(List<Product> products) {
        this.products = products;
        this.total = calculateTotal(); 
    }

    /**
     * Gets the final total price of the sale.
     *
     * @return the final total amount
     */
    public double getTotal() { 
        return total; 
    }

    /**
     * Sets the final total price of the sale.
     *
     * @param total the final total amount to set
     */
    public void setTotal(double total) { 
        this.total = total; 
    }

    /**
     * Gets the name of the applied promotion.
     *
     * @return the applied promotion name
     */
    public String getAppliedPromotionName() { 
        return appliedPromotionName; 
    }

    /**
     * Sets the name of the applied promotion.
     *
     * @param appliedPromotionName the promotion name to set
     */
    public void setAppliedPromotionName(String appliedPromotionName) { 
        this.appliedPromotionName = appliedPromotionName; 
    }

    /**
     * Gets the monetary discount amount applied to the sale.
     *
     * @return the discount amount
     */
    public double getDiscountAmount() { 
        return discountAmount; 
    }

    /**
     * Sets the monetary discount amount applied to the sale.
     *
     * @param discountAmount the discount amount to set
     */
    public void setDiscountAmount(double discountAmount) { 
        this.discountAmount = discountAmount; 
    }

    /**
     * Gets the cumulative cost of extended warranties requested in this sale.
     *
     * @return the total extended warranty cost
     */
    public double getExtendedWarrantyCost() { 
        return extendedWarrantyCost; 
    }

    /**
     * Sets the cumulative cost of extended warranties.
     *
     * @param extendedWarrantyCost the extended warranty cost to set
     */
    public void setExtendedWarrantyCost(double extendedWarrantyCost) { 
        this.extendedWarrantyCost = extendedWarrantyCost; 
    }
}