package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;

/**
 * Utility class providing validation methods for Product entities and inputs.
 */
public class ProductValidator {

    private ProductValidator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates primitive attributes required for a product.
     * @param id
     * @param title
     * @param price
     * @param stock
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("ID del producto", "no puede estar vacío");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Título", "no puede estar vacío");
        }
        if (price <= 0) {
            throw new InvalidDataException("Precio", "debe ser mayor a cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("Stock", "no puede ser negativo");
        }
    }

    /**
     * Validates that requested quantity is available in stock.
     * @param product
     * @param requestedQuantity
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (product != null && product.getStock() < requestedQuantity) {
            throw new BusinessRuleException(
                String.format("Stock insuficiente para el producto '%s'. Stock actual: %d, Solicitado: %d",
                    product.getTitle(), product.getStock(), requestedQuantity)
            );
        }
    }

    /**
     * Validates that a product instance exists.
     * @param product
     * @param productId
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }
}