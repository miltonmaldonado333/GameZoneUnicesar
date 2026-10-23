package com.gamezone.service;

import com.gamezone.model.Product;

/**
 * Stock operations on a given item. The item is passed itself, and not only its
 * identifier, so the implementation can find the service that holds it without
 * testing the type of the item.
 */
public interface ItemStock {

    /**
     * Reduces the stock of the given item.
     *
     * @param item     the item whose stock is reduced
     * @param quantity the quantity to deduct
     */
    void reduceStock(Product item, int quantity);

    /**
     * Restores the stock of the given item.
     *
     * @param item     the item whose stock is restored
     * @param quantity the quantity to return to inventory
     */
    void restoreStock(Product item, int quantity);
}