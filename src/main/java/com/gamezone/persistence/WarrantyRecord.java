package com.gamezone.persistence;

import java.time.LocalDate;

/**
 * Raw representation of a stored warranty. It holds the identifiers read from
 * the file instead of model objects, so the repository does not depend on any
 * service. The service layer rebuilds the Warranty from these identifiers.
 */
public class WarrantyRecord {

    private final String type;
    private final String id;
    private final String productId;
    private final String saleId;
    private final LocalDate startDate;

    /**
     * Creates a record with the values read from one line of the warranties file.
     *
     * @param type      the stored type discriminator, BASIC or EXTENDED
     * @param id        the warranty identifier
     * @param productId the identifier of the covered product
     * @param saleId    the identifier of the sale
     * @param startDate the start date of the warranty
     */
    public WarrantyRecord(String type, String id, String productId, String saleId, LocalDate startDate) {
        this.type = type;
        this.id = id;
        this.productId = productId;
        this.saleId = saleId;
        this.startDate = startDate;
    }

    /**
     * Gets the stored type discriminator.
     *
     * @return BASIC or EXTENDED
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the warranty identifier.
     *
     * @return the warranty identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the identifier of the covered product.
     *
     * @return the product identifier
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Gets the identifier of the sale.
     *
     * @return the sale identifier
     */
    public String getSaleId() {
        return saleId;
    }

    /**
     * Gets the start date of the warranty.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
}