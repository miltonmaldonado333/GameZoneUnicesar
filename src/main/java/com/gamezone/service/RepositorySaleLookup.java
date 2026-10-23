package com.gamezone.service;

import java.util.List;

import com.gamezone.model.Sale;
import com.gamezone.persistence.ISaleRepository;

/**
 * Sale lookup backed by the sale repository. It depends only on the repository
 * abstraction, so it can be built before the services that use it.
 */
public class RepositorySaleLookup implements SaleLookup {

    private final ISaleRepository saleRepository;

    /**
     * Creates a lookup over the given sale repository.
     *
     * @param saleRepository the repository the sales are read from
     */
    public RepositorySaleLookup(ISaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    /**
     * Finds a recorded sale by its identifier, compared as text.
     *
     * @param saleId the sale identifier
     * @return the matching sale, or null if there is none
     */
    @Override
    public Sale findSaleById(String saleId) {
        for (Sale sale : saleRepository.findAll()) {
            if (String.valueOf(sale.getId()).equals(saleId)) {
                return sale;
            }
        }
        return null;
    }

    /**
     * Retrieves all recorded sales from the repository.
     *
     * @return the list of recorded sales
     */
    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}