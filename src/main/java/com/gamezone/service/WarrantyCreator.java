package com.gamezone.service;

import java.time.LocalDate;

import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;

/**
 * Creates the warranty of one type from the values read from a stored record,
 * so the record mapper rebuilds each type without testing its stored text in a
 * chain of conditions.
 */
@FunctionalInterface
public interface WarrantyCreator {

    /**
     * Creates a warranty.
     *
     * @param id        the warranty identifier
     * @param product   the covered product
     * @param sale      the sale of the warranty
     * @param startDate the start date of the warranty
     * @return the created warranty
     */
    Warranty create(String id, Product product, Sale sale, LocalDate startDate);
}