package com.gamezone.model;

import java.time.LocalDate;

/**
 * Abstract base class representing a generic promotion in the system.
 */
public abstract class Promotion {
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Checks if the promotion is active on a given date.
     * @param date the date to check
     * @return true if the date is within the promotion's start and end dates, false otherwise
     */
    public boolean isActive(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) && 
               (date.isEqual(endDate) || date.isBefore(endDate));
    }

    /**
     * Calculates the monetary discount for a specific sale.
     * @param sale the sale to evaluate
     * @return the discount amount in local currency
     */
    public abstract double calculateDiscount(Sale sale);

    // Genera aquí los Getters y Setters para id, name, startDate y endDate
    public String getId() {
    return id;
}

public void setId(String id) {
    this.id = id;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public LocalDate getStartDate() {
    return startDate;
}

public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
}

public LocalDate getEndDate() {
    return endDate;
}

public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
}
}