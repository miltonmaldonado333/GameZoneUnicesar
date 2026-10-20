package com.gamezone.persistence;

import java.time.LocalDate;
import java.util.List;

/**
 * Raw representation of a stored return. It holds the identifiers read from the
 * file instead of model objects, so the repository does not depend on any
 * service. The service layer rebuilds the Return from these identifiers.
 */
public class ReturnRecord {

    private final String returnId;
    private final LocalDate date;
    private final String saleId;
    private final List<String> productIds;
    private final String reason;
    private final double warrantyRefundAmount;

    /**
     * Creates a record with the values read from one line of the returns file.
     *
     * @param returnId             the return identifier
     * @param date                 the date the return was registered
     * @param saleId               the identifier of the original sale
     * @param productIds           the identifiers of the returned items
     * @param reason               the reason for the return
     * @param warrantyRefundAmount the amount refunded for cancelled warranties
     */
    public ReturnRecord(String returnId, LocalDate date, String saleId, List<String> productIds,
            String reason, double warrantyRefundAmount) {
        this.returnId = returnId;
        this.date = date;
        this.saleId = saleId;
        this.productIds = productIds;
        this.reason = reason;
        this.warrantyRefundAmount = warrantyRefundAmount;
    }

    /**
     * Gets the return identifier.
     *
     * @return the return identifier
     */
    public String getReturnId() {
        return returnId;
    }

    /**
     * Gets the date the return was registered.
     *
     * @return the return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the identifier of the original sale.
     *
     * @return the sale identifier
     */
    public String getSaleId() {
        return saleId;
    }

    /**
     * Gets the identifiers of the returned items.
     *
     * @return the list of item identifiers
     */
    public List<String> getProductIds() {
        return productIds;
    }

    /**
     * Gets the reason for the return.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the amount refunded for cancelled warranties.
     *
     * @return the warranty refund amount
     */
    public double getWarrantyRefundAmount() {
        return warrantyRefundAmount;
    }
}