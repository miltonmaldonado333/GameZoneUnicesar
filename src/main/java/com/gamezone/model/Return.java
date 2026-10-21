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

    /**
     * Constructs a new Return with no warranty refund and automatically
     * calculates the refund amount.
     *
     * @param returnId         unique identifier of the return
     * @param date             date the return was registered
     * @param originalSale     the sale the products were bought in
     * @param returnedProducts the products being returned
     * @param reason           the reason for the return
     */
    public Return(String returnId, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this(returnId, date, originalSale, returnedProducts, reason, 0.0);
    }

    /**
     * Constructs a new Return including the amount refunded for cancelled
     * warranties and automatically calculates the total refund amount.
     *
     * @param returnId             unique identifier of the return
     * @param date                 date the return was registered
     * @param originalSale         the sale the products were bought in
     * @param returnedProducts     the products being returned
     * @param reason               the reason for the return
     * @param warrantyRefundAmount amount refunded for the cancelled warranties
     */
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

    /**
     * Generates a formatted return receipt in Spanish for display or printing.
     * Itemizes each returned product showing its list price, the proportional
     * discount applied during the sale and the net refunded amount, followed by
     * the amount refunded for cancelled warranties and the total.
     *
     * @return the formatted receipt string
     */
    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== RECIBO DE DEVOLUCION ===\n");
        receipt.append("ID Devolucion: ").append(this.returnId).append("\n");
        receipt.append("Fecha: ").append(this.date).append("\n");
        receipt.append("ID Venta Original: ").append(this.originalSale != null ? this.originalSale.getId() : "N/A").append("\n");
        receipt.append("Motivo: ").append(this.reason).append("\n");
        receipt.append("=== Productos Devueltos ===\n");

        double saleSubtotal = (originalSale != null) ? originalSale.getSubtotal() : 0.0;
        double discountAmount = (originalSale != null) ? originalSale.getDiscountAmount() : 0.0;
        double discountRate = (saleSubtotal > 0) ? (discountAmount / saleSubtotal) : 0.0;

        if (this.returnedProducts != null) {
            for (Product product : this.returnedProducts) {
                double originalPrice = product.getPrice();
                double itemDiscount = originalPrice * discountRate;
                double itemRefund = originalPrice - itemDiscount;

                if (discountRate > 0) {
                    receipt.append(String.format("- %s: Precio Lista: $%.2f | Desc. Aplicado: -$%.2f | Reembolso: $%.2f%n",
                            product.getTitle(), originalPrice, itemDiscount, itemRefund));
                } else {
                    receipt.append(String.format("- %s: $%.2f%n", product.getTitle(), originalPrice));
                }
            }
        }

        if (this.warrantyRefundAmount > 0) {
            receipt.append(String.format("Reembolso por garantias extendidas: $%.2f%n", this.warrantyRefundAmount));
        }

        receipt.append("=================\n");
        receipt.append(String.format("Monto Reembolsado Total: $%.2f%n", this.refundAmount));
        receipt.append("=================\n");

        return receipt.toString();
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