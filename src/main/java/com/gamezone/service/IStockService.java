package com.gamezone.service;

/**
 * Service contract for unified stock and inventory management operations.
 * Decouples sales and transaction processing from concrete product implementations.
 */
public interface IStockService {

    /**
     * Checks whether sufficient stock is available for a specified product.
     *
     * @param productId the unique identifier of the product
     * @param requestedQuantity the quantity required for the transaction
     * @return true if available stock is greater than or equal to requested quantity
     */
    boolean hasSufficientStock(String productId, int requestedQuantity);

    /**
     * Reduces the stock level of a product by the specified quantity.
     *
     * @param productId the unique identifier of the product
     * @param quantity the quantity to deduct from inventory
     */
    void reduceStock(String productId, int quantity);

    /**
     * Restores or increases the stock level of a product by the specified quantity.
     *
     * @param productId the unique identifier of the product
     * @param quantity the quantity to return to inventory
     */
    void restoreStock(String productId, int quantity);
}