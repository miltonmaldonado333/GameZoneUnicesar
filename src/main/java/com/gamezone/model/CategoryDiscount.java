package com.gamezone.model;

import java.time.LocalDate;

public class CategoryDiscount extends Promotion {

    private double percentage;
    private String targetCategory;

    public CategoryDiscount(double percentage, String targetCategory, String id, String name, LocalDate startDate, LocalDate endDate) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
        this.targetCategory = targetCategory;
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
        this.targetCategory = targetCategory;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        double applicableSubtotal = 0.0;
        for (Product product : sale.getProducts()) {
            if ("VIDEOGAME".equalsIgnoreCase(targetCategory) && product instanceof VideoGame) {
                applicableSubtotal += product.getPrice();
            } else if ("CONSOLE".equalsIgnoreCase(targetCategory) && product instanceof Console) {
                applicableSubtotal += product.getPrice();
            }

        }
        return applicableSubtotal * (percentage / 100);
    }

    @Override
    public String getDetails() {
        return String.format("Type: Category | Target Category: %s | Discount: %.1f%%", targetCategory, percentage);
    }
}
