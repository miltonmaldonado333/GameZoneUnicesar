package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.model.Return;

/**
 * Utility class responsible for formatting and generating return transaction receipts for UI presentation.
 */
public class ReturnReceiptPrinter {

    private ReturnReceiptPrinter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a formatted text receipt for a return transaction.
     * 
     * @param returnModel the return entity to format
     * @return formatted receipt string
     */
    public static String generateReturnReceipt(Return returnModel) {
        if (returnModel == null) {
            return "";
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("=== RECIBO DE DEVOLUCION ===\n");
        receipt.append("ID Devolucion: ").append(returnModel.getReturnId()).append("\n");
        receipt.append("Fecha: ").append(returnModel.getDate()).append("\n");
        receipt.append("ID Venta Original: ")
               .append(returnModel.getOriginalSale() != null ? returnModel.getOriginalSale().getId() : "N/A")
               .append("\n");
        receipt.append("Motivo: ").append(returnModel.getReason()).append("\n");
        receipt.append("=== Productos Devueltos ===\n");

        double saleSubtotal = (returnModel.getOriginalSale() != null) ? returnModel.getOriginalSale().getSubtotal() : 0.0;
        double discountAmount = (returnModel.getOriginalSale() != null) ? returnModel.getOriginalSale().getDiscountAmount() : 0.0;
        double discountRate = (saleSubtotal > 0) ? (discountAmount / saleSubtotal) : 0.0;

        if (returnModel.getReturnedProducts() != null) {
            for (Product product : returnModel.getReturnedProducts()) {
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

        if (returnModel.getWarrantyRefundAmount() > 0) {
            receipt.append(String.format("Reembolso por garantias extendidas: $%.2f%n", returnModel.getWarrantyRefundAmount()));
        }

        receipt.append("=================\n");
        receipt.append(String.format("Monto Reembolsado Total: $%.2f%n", returnModel.getRefundAmount()));
        receipt.append("=================\n");

        return receipt.toString();
    }
}