package com.gamezone.validation;

import java.util.List;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;

/**
 * Utility class providing validation methods for product returns.
 * The checks keep the exception type and the messages that ReturnService used
 * before they were extracted, so the behaviour seen by the user is unchanged.
 */
public class ReturnValidator {

    private ReturnValidator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates that the original sale exists and is inside the 30-day return window.
     *
     * @param sale   the original sale, or null if it was not found
     * @param saleId the identifier that was searched
     * @throws IllegalArgumentException if the sale does not exist or the 30-day window has passed
     */
    public static void validateReturnableSale(Sale sale, String saleId) {
        if (sale == null) {
            throw new IllegalArgumentException("The specified sale does not exist: " + saleId);
        }
        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("The sale has exceeded the 30-day return policy window.");
        }
    }

    /**
     * Validates that a product belongs to the original sale.
     *
     * @param product   the product found in the sale, or null if it is not part of it
     * @param productId the identifier that was searched
     * @param saleId    the identifier of the original sale
     * @throws IllegalArgumentException if the product does not belong to the sale
     */
    public static void validateProductBelongsToSale(Product product, String productId, String saleId) {
        if (product == null) {
            throw new IllegalArgumentException("Product " + productId + " does not belong to sale " + saleId);
        }
    }

    /**
     * Validates that the units requested for a product, added to the units already
     * returned, do not exceed the units purchased in the sale.
     *
     * @param sale            the original sale
     * @param previousReturns the returns already registered for that sale
     * @param requestedIds    the identifiers requested in the current return
     * @param productId       the identifier of the product being checked
     * @param saleId          the identifier of the original sale
     * @throws IllegalArgumentException if no units are left to return
     */
    public static void validateReturnableQuantity(Sale sale, List<Return> previousReturns,
            List<String> requestedIds, String productId, String saleId) {
        long originalQuantity = 0;
        for (Product p : sale.getProducts()) {
            if (p.getId().equalsIgnoreCase(productId)) {
                originalQuantity++;
            }
        }

        long alreadyReturnedQuantity = 0;
        for (Return previous : previousReturns) {
            for (Product p : previous.getReturnedProducts()) {
                if (p.getId().equalsIgnoreCase(productId)) {
                    alreadyReturnedQuantity++;
                }
            }
        }

        long requestedInCurrentBatch = 0;
        for (String id : requestedIds) {
            if (id.equalsIgnoreCase(productId)) {
                requestedInCurrentBatch++;
            }
        }

        if (alreadyReturnedQuantity + requestedInCurrentBatch > originalQuantity) {
            throw new IllegalArgumentException("Product " + productId
                    + " has no units available for return in sale " + saleId
                    + " (Purchased: " + originalQuantity + ", Previously returned: " + alreadyReturnedQuantity + ").");
        }
    }
}