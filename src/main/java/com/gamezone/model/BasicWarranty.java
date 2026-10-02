
package com.gamezone.model;

import java.time.LocalDate;

public class BasicWarranty extends Warranty {

    /**
     * Constructs a new BasicWarranty.
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }
    

    @Override
    public int getDurationInMonths() {
        return 6;
    }

    @Override
    public String getWarrantyType() {
        return "Garantia Basica";
    }

    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
    
}
