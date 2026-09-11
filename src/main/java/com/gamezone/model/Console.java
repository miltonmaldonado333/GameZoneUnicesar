
package com.gamezone.model;

/**
 * Model class representing a gaming console product.
 * Extends base Product class with console-specific attributes.
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
   
    @Override
    public String getFullDescription(){
       return String.format("Console: [ID: %s, Title: %s, Price: $%.2f, Stock: %d, Brand: %s, Model: %s, Generation: %s]",getId(), getTitle(), getPrice(), getStock(), brand, model,generation);
    } 
}
