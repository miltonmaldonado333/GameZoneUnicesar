package com.gamezone.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class representing a generic promotion in the system.
 */
public abstract class Promotion {

    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Checks if the promotion is active on a given date.
     *
     * @param date the date to check
     * @return true if the date is within the promotion's start and end dates,
     *         false otherwise
     */
    public boolean isActive(LocalDate date) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return (date.isEqual(startDate) || date.isAfter(startDate))
                && (date.isEqual(endDate) || date.isBefore(endDate));
    }

    /**
     * Checks if the promotion is active on the current system date.
     *
     * @return true if today is within the promotion's start and end dates,
     *         false otherwise
     */
    public boolean isActive() {
        return isActive(LocalDate.now());
    }

    /**
     * Formats the promotion date range in dd/MM/yyyy format.
     *
     * @return formatted date range string (e.g. "15/10/2026 a 29/10/2026")
     */
    public String getFormattedDateRange() {
        if (startDate == null || endDate == null) {
            return "N/A";
        }
        return startDate.format(DATE_FORMATTER) + " a " + endDate.format(DATE_FORMATTER);
    }

    /**
     * Calculates the monetary discount for a specific sale.
     *
     * @param sale the sale to evaluate
     * @return the discount amount in local currency
     */
    public abstract double calculateDiscount(Sale sale);

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

    /**
     * Abstract method to get specific details about the promotion rules.
     *
     * @return a formatted string with promotion details
     */
    public abstract String getDetails();

}