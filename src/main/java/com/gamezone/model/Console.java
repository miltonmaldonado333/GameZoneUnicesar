package com.gamezone.model;

/**
 * Model class representing a gaming console product in the GameZone system.
 * Extends the base {@link Product} class with hardware-specific attributes.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    /**
     * Default constructor.
     */
    public Console() {
    }

    /**
     * Parameterized constructor initializing base product attributes and console properties.
     *
     * @param brand      the manufacturer brand (e.g., Sony, Microsoft, Nintendo)
     * @param model      the specific console model
     * @param generation the technological generation of the console
     * @param id         the unique product identifier
     * @param title      the display title of the product
     * @param price      the unit price
     * @param stock      the inventory quantity
     */
    public Console(String brand, String model, String generation, String id, String title, double price, int stock) {
        super(id, title, price, stock);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    /**
     * Returns the category identifier for console products.
     *
     * @return the category name "Console"
     */
    @Override
    public String getCategory() {
        return "Console";
    }

    /**
     * Constructs a full formatted description string including console-specific properties.
     *
     * @return a detailed string representation of the console
     */
    @Override
    public String getFullDescription() {
        return String.format("Console: [ID: %s, Title: %s, Price: $%.2f, Stock: %d, Brand: %s, Model: %s, Generation: %s]",
                getId(), getTitle(), getPrice(), getStock(), brand, model, generation);
    }
}