package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents the return of one or more products from an original sale.
 * The refund covers the returned items (proportionally reduced by the sale
 * discount) plus the refundable cost of any warranties cancelled because of
 * the return.
 */
public class Return {

    private String returnId;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double warrantyRefundAmount;
    private double refundAmount;

    public Return(String returnId, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this(returnId, date, originalSale, returnedProducts, reason, 0.0);
    }

    public Return(String returnId, LocalDate date, Sale originalSale, List<Product> returnedProducts,
                  String reason, double warrantyRefundAmount) {
        this.returnId = returnId;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.warrantyRefundAmount = warrantyRefundAmount;
        this.calculateRefundAmount();
    }

    /**
     * Calculates the total refund amount: the returned items, reduced in
     * proportion to the discount of the original sale, plus the refundable
     * cost of the cancelled warranties.
     *
     * GRASP Information Expert: Delegates sale subtotal calculation to Sale
     * instead of manually iterating over sale.getProducts() (V-12).
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double rawSubtotal = 0.0;
        if (returnedProducts != null) {
            for (Product product : returnedProducts) {
                rawSubtotal += product.getPrice();
            }
        }

        double itemsRefund = rawSubtotal;
        if (originalSale != null) {
            double saleSubtotal = originalSale.getSubtotal();
            double discountAmount = originalSale.getDiscountAmount();

            if (saleSubtotal > 0 && discountAmount > 0) {
                double discountRate = discountAmount / saleSubtotal;
                itemsRefund = rawSubtotal * (1.0 - discountRate);
            }
        }

        this.refundAmount = itemsRefund + this.warrantyRefundAmount;
        return this.refundAmount;
    }

    public String getReturnId() {
        return returnId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Sale getOriginalSale() {
        return originalSale;
    }

    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    public String getReason() {
        return reason;
    }

    public double getWarrantyRefundAmount() {
        return warrantyRefundAmount;
    }

    public double getRefundAmount() {
        return refundAmount;
    }
}