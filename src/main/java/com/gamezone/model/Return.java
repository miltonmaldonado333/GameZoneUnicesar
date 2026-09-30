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
        if (originalSale != null && originalSale.getProducts() != null && !originalSale.getProducts().isEmpty()) {
            double saleSubtotal = 0.0;
            for (Product p : originalSale.getProducts()) {
                saleSubtotal += p.getPrice();
            }
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

        double saleSubtotal = 0.0;
        double discountAmount = 0.0;
        if (originalSale != null && originalSale.getProducts() != null) {
            for (Product p : originalSale.getProducts()) {
                saleSubtotal += p.getPrice();
            }
            discountAmount = originalSale.getDiscountAmount();
        }

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

    /**
     * Gets the return identifier.
     *
     * @return the return ID
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
     * Gets the sale the returned products belong to.
     *
     * @return the original sale
     */
    public Sale getOriginalSale() {
        return originalSale;
    }

    /**
     * Gets the products being returned.
     *
     * @return the list of returned products
     */
    public List<Product> getReturnedProducts() {
        return returnedProducts;
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
     * Gets the amount refunded for warranties cancelled by this return.
     *
     * @return the warranty refund amount
     */
    public double getWarrantyRefundAmount() {
        return warrantyRefundAmount;
    }

    /**
     * Gets the total refunded amount (items plus warranties).
     *
     * @return the total refund amount
     */
    public double getRefundAmount() {
        return refundAmount;
    }
}