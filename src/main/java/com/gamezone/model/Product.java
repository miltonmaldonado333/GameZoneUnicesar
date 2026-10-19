package com.gamezone.model;

/**
 * Abstract base class representing a generic product in the GameZone inventory.
 * Serves as the superclass for concrete product types like consoles, video games, and accessories.
 */
public abstract class Product {

    private String id;
    private String title;
    private double price;
    private int stock;

    /**
     * Default constructor.
     */
    public Product() {
    }

    /**
     * Constructs a Product with specified core parameters.
     *
     * @param id    the unique product identifier
     * @param title the title or display name of the product
     * @param price the unit price of the product
     * @param stock the current inventory quantity available
     */
    public Product(String id, String title, double price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Abstract method to obtain a detailed string description of the product.
     *
     * @return a formatted string with all product attributes
     */
    public abstract String getFullDescription();

    /**
     * Abstract method to retrieve the product category or classification.
     *
     * @return the category name used for filtering and promotion rules
     */
    public abstract String getCategory();

    /**
     * Increases stock by the specified quantity.
     *
     * @param quantity quantity to add
     */
    public void addStock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
        }
    }

    /**
     * Deducts stock from the product if sufficient quantity is available.
     *
     * @param quantity quantity to subtract
     */
    public void reduceStock(int quantity) {
        if (quantity > 0 && this.stock >= quantity) {
            this.stock -= quantity;
        }
    }

    /**
     * Indicates whether this product is eligible for extended warranty coverage.
     * Default implementation returns false; overridden in specific subclasses like Console (V-05).
     *
     * @return true if extended warranty applies; false by default
     */
    public boolean hasWarrantyCoverage() {
        return false;
    }
}