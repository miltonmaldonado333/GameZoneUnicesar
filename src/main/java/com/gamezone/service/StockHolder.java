package com.gamezone.service;

/**
 * Stock contract of a service that owns a set of items and can tell whether an
 * identifier belongs to it. It lets a resolver route a stock operation to the
 * service that holds the item without testing the type of the item.
 */
public interface StockHolder extends IStockService {

    /**
     * Tells whether the given identifier belongs to the items held by this service.
     *
     * @param itemId the identifier of the item
     * @return true if this service holds an item with that identifier
     */
    boolean holds(String itemId);
}