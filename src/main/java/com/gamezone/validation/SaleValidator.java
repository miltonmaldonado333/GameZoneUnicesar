package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;

import java.util.List;

/**
 * Utility class providing validation methods for Sale operations and entities.
 */
public class SaleValidator {

    private SaleValidator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates client, seller, and product list for a sale transaction.
     * 
     * @param client  person entity acting as the buyer
     * @param seller  person entity acting as the seller
     * @param products list of products included in the sale
     */
    public static void validateSaleData(Person client, Person seller, List<Product> products) {
        if (client == null) {
            throw new InvalidDataException("Cliente", "el cliente asociado no puede ser nulo");
        }
        if (seller == null) {
            throw new InvalidDataException("Vendedor", "el vendedor asociado no puede ser nulo");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException("No se puede registrar una venta sin productos.");
        }
    }

    /**
     * Validates that a sale instance exists.
     * @param sale
     * @param saleId
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}