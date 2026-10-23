package com.gamezone.service;

/**
 * Narrow abstraction for cancelling the warranties of a returned product, so
 * the services that register returns do not depend on the whole warranty service.
 */
public interface WarrantyCanceller {

    /**
     * Cancels every warranty attached to the given product within the given sale.
     *
     * @param productId unique identifier of the returned product
     * @param saleId    unique identifier of the sale the product was bought in
     * @return the refundable cost of the cancelled warranties, or 0.0 if there was none
     */
    double cancelWarranties(String productId, String saleId);
}