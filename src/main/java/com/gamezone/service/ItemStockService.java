package com.gamezone.service;

import com.gamezone.model.Product;

/**
 * Stock service that routes every operation to the service that holds the item.
 * The owner is decided by asking the accessory stock whether it holds the very
 * item, and not by comparing identifiers, because a product and an accessory can
 * share an identifier. Callers reduce or restore stock without testing the type
 * of the item.
 */
public class ItemStockService implements ItemStock {

    private final IStockService productStock;
    private final StockHolder accessoryStock;

    /**
     * Creates a resolver over the stock of the products and the stock of the accessories.
     *
     * @param productStock   the stock contract of the products
     * @param accessoryStock the stock contract of the accessories, which can tell whether it holds an item
     */
    public ItemStockService(IStockService productStock, StockHolder accessoryStock) {
        this.productStock = productStock;
        this.accessoryStock = accessoryStock;
    }

    private IStockService ownerOf(Product item) {
        return accessoryStock.holds(item) ? accessoryStock : productStock;
    }

    /**
     * Reduces the stock in the service that holds the item.
     *
     * @param item     the item whose stock is reduced
     * @param quantity the quantity to deduct
     */
    @Override
    public void reduceStock(Product item, int quantity) {
        ownerOf(item).reduceStock(item.getId(), quantity);
    }

    /**
     * Restores the stock in the service that holds the item.
     *
     * @param item     the item whose stock is restored
     * @param quantity the quantity to return to inventory
     */
    @Override
    public void restoreStock(Product item, int quantity) {
        ownerOf(item).restoreStock(item.getId(), quantity);
    }
}