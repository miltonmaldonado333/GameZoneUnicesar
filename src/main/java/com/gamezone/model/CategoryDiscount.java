package com.gamezone.model;

import java.time.LocalDate;

public class CategoryDiscount extends Promotion {

    private double percentage;
    private String targetCategory;

    public CategoryDiscount(double percentage, String targetCategory, String id, String name, LocalDate startDate, LocalDate endDate) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
        setTargetCategory(targetCategory);
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public void setTargetCategory(String targetCategory) {
        if (!"VIDEOGAME".equalsIgnoreCase(targetCategory)
                && !"CONSOLE".equalsIgnoreCase(targetCategory)
                && !"ACCESSORY".equalsIgnoreCase(targetCategory)) {
            throw new IllegalArgumentException("La categoría objetivo debe ser VIDEOGAME, CONSOLE o ACCESSORY.");
        }
        this.targetCategory = targetCategory.toUpperCase();
    }

    /**
     * Calculates discount by checking product categories polymorphically 
     * without inspecting concrete instances via instanceof (V-08, V-09).
     */
    @Override
    public double calculateDiscount(Sale sale) {
        if (sale == null || sale.getProducts() == null) {
            return 0.0;
        }

        double applicableSubtotal = 0.0;

        for (Product product : sale.getProducts()) {
            if (product != null) {
                // DEBUG: Imprime en consola para diagnosticar la comparación
                System.out.println("DEBUG - ID Producto: " + product.getId() 
           + " | Categoria del producto: '" + product.getCategory() + "'" 
           + " | Target: '" + targetCategory + "'");

                if (targetCategory.equalsIgnoreCase(product.getCategory())) {
                    applicableSubtotal += product.getPrice();
                }
            }
        }

        return applicableSubtotal * (percentage / 100);
    }

    @Override
    public String getDetails() {
        return String.format("Type: Category | Target Category: %s | Discount: %.1f%%", targetCategory, percentage);
    }
}