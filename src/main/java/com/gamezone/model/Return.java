package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

public class Return {

    private String returnId;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;
    /**
     * Calculates the total refund amount based on the prices of the returned products.
     * 
     * @return the calculated refund amount
     */
    public Return(String returnId, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason, double refundAmount) {
        this.returnId = returnId;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = refundAmount;
    }

    public double calculateRefundAmount() {
        double total = 0.0;
        if (this.returnedProducts != null) {
            for (Product product : this.returnedProducts) {
                total += product.getPrice();
            }
        }
        this.refundAmount = total;
        return this.refundAmount;
    }
    /**
     * Generates a formatted return receipt in Spanish for the customer.
     * 
     * @return the formatted receipt string
     */
    public String generateReturnReceipt(){
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== RECIBO DE DEVOLUCION ===\n");
        receipt.append("ID Devolucion: ").append(this.returnId).append("\n");
        receipt.append("Fecha: ").append(this.date).append("\n");
        receipt.append("ID Venta Original: ").append(this.originalSale).append("\n");
        receipt.append("Motivo: ").append(this.reason).append("\n");
        receipt.append("=== Productos Devueltos ===\n");
        
        for(Product product : this.returnedProducts){
            receipt.append("- ").append(product.getTitle()).append(": $ ").append(product.getPrice()).append("\n");
            
        }
        
        receipt.append("=================");
        receipt.append("Monto Rembolsado: $ ").append(this.refundAmount).append("\n");
        receipt.append("=================");
        
        return receipt.toString();
        
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Sale getOriginalSale() {
        return originalSale;
    }

    public void setOriginalSale(Sale originalSale) {
        this.originalSale = originalSale;
    }

    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    public void setReturnedProducts(List<Product> returnedProducts) {
        this.returnedProducts = returnedProducts;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }
    
}
