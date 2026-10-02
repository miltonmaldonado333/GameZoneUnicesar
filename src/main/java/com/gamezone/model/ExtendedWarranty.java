package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents an extended warranty that covers defects and accidents for 12 months at an additional cost.
 */
public class ExtendedWarranty extends Warranty {

    /**
     * Constructs a new ExtendedWarranty.
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return 12;
    }

    @Override
    public String getWarrantyType() {
        return "Garantia Extendida";
    }

    @Override
    public double getAdditionalCost() {
        return this.getProduct().getPrice() * 0.10;
    }
}