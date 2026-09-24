
package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that applies a flat percentage discount to the entire sale total.
 */
public class PercentageDiscount extends Promotion {
    private double percentage;

    public PercentageDiscount(double percentage, String id, String name, LocalDate startDate, LocalDate endDate) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    

    @Override
    public double calculateDiscount(Sale sale) {
       return sale.calculateTotal()*(percentage / 100);
    }
    
    @Override
public String getDetails() {
    return String.format("Type: Percentage | Discount: %.1f%%", percentage);
}
            
}
