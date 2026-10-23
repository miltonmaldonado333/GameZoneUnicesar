package com.gamezone.service;

/**
 * Stock service that routes every operation to the service that holds the item,
 * so callers reduce or restore stock through the common IStockService contract
 * without testing the type of the item. Accessories are asked first; any other
 * identifier goes to the product stock, which keeps its own error for unknown ids.
 */
public class ItemStockService implements IStockService {

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

    private IStockService route(String itemId) {
        return accessoryStock.holds(itemId) ? accessoryStock : productStock;
    }

    /**
     * Checks the stock in the service that holds the item.
     *
     * @param productId         the identifier of the item
     * @param requestedQuantity the quantity required
     * @return true if the stock is greater than or equal to the requested quantity
     */
    @Override
    public boolean hasSufficientStock(String productId, int requestedQuantity) {
        return route(productId).hasSufficientStock(productId, requestedQuantity);
    }

    /**
     * Reduces the stock in the service that holds the item.
     *
     * @param productId the identifier of the item
     * @param quantity  the quantity to deduct
     */
    @Override
    public void reduceStock(String productId, int quantity) {
        route(productId).reduceStock(productId, quantity);
    }

    /**
     * Restores the stock in the service that holds the item.
     *
     * @param productId the identifier of the item
     * @param quantity  the quantity to return to inventory
     */
    @Override
    public void restoreStock(String productId, int quantity) {
        route(productId).restoreStock(productId, quantity);
    }
}