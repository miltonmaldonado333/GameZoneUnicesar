package com.gamezone.model;

import java.time.LocalDate;

public abstract class Warranty {

    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    
    public Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    /**
     * Gets the duration of the warranty in months.
     *
     * @return the number of months the warranty is valid
     */
    public abstract int getDurationInMonths();

    /**
     * Gets the name of the warranty type.
     *
     * @return the warranty type name
     */
    public abstract String getWarrantyType();

    /**
     * Gets the additional cost of the warranty.
     *
     * @return the additional cost
     */
    public abstract double getAdditionalCost();

    /**
     * Checks if the warranty is active on a given date.
     *
     * @return true if the date is between the start and end dates
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Generates a formatted warranty certificate in Spanish.
     *
     * @return the formatted certificate string
     */
    public String generateWarrantyCertificate() {
        StringBuilder certificate = new StringBuilder();
        certificate.append("=== WARRABTY CERTIFICATE ===\n");
        certificate.append("Warranty ID: ").append(this.id).append("\n");
        certificate.append("Tipe: ").append(getWarrantyType()).append("\n");
        certificate.append("Product: ").append(this.product.getTitle()).append("\n");
        certificate.append("Sale ID: ").append(this.sale.getId()).append("\n");
        certificate.append("Start Date: ").append(this.startDate).append("\n");
        certificate.append("Finish Date: ").append(this.endDate).append("\n");
        certificate.append("Additional value $: ").append(getAdditionalCost()).append("\n");
        return certificate.toString();
    }
    
// getersn and setters
    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Sale getSale() {
        return sale;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

}
