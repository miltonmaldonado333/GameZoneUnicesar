package com.gamezone.service;

import com.gamezone.model.Product;

/**
 * Stock contract of a service that owns a set of items and can tell whether it
 * holds a given item. It lets a resolver route a stock operation to the service
 * that holds the item without testing the type of the item.
 */
public interface StockHolder extends IStockService {

    /**
     * Tells whether the given item is one of the items held by this service. The
     * check is made on the item itself and not on its identifier, because a
     * product and an accessory can share an identifier.
     *
     * @param item the item to look for
     * @return true if this service holds that very item
     */
    boolean holds(Product item);
}