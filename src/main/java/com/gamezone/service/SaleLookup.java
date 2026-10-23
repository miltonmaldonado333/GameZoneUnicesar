package com.gamezone.service;

import java.util.List;

import com.gamezone.model.Sale;

/**
 * Narrow abstraction that lets a service read the recorded sales without
 * depending on the sale service, so the services that need the sales and the
 * service that registers them do not depend on each other.
 */
public interface SaleLookup {

    /**
     * Finds a recorded sale by its identifier.
     *
     * @param saleId the sale identifier, compared as text
     * @return the matching sale, or null if there is none
     */
    Sale findSaleById(String saleId);

    /**
     * Retrieves all recorded sales.
     *
     * @return the list of recorded sales
     */
    List<Sale> getAllSales();
}