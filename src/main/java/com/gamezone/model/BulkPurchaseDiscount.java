
package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that applies a percentage discount if the sale meets or exceeds a minimum item quantity.
 */
public class BulkPurchaseDiscount extends Promotion {
    private int minQuantity;
    private double percentage;

    public BulkPurchaseDiscount(int minQuantity, double percentage, String id, String name, LocalDate startDate, LocalDate endDate) {
        super(id, name, startDate, endDate);
        this.minQuantity = minQuantity;
        this.percentage = percentage;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    

    @Override
    public double calculateDiscount(Sale sale) {
        if(sale.getProducts().size() >= minQuantity){
            return sale.calculateTotal()*(percentage/100);
        }
       return 0.0;
    }
}
