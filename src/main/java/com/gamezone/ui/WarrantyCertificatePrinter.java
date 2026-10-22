package com.gamezone.ui;

import com.gamezone.model.Warranty;

public class WarrantyCertificatePrinter {

    private WarrantyCertificatePrinter() {
        // Constructor privado
    }

    public static String generateWarrantyCertificate(Warranty warranty) {
        if (warranty == null) {
            return "";
        }
        
        StringBuilder certificate = new StringBuilder();
        certificate.append("=== WARRANTY CERTIFICATE ===\n");
        certificate.append("Warranty ID: ").append(warranty.getId()).append("\n");
        certificate.append("Type: ").append(warranty.getWarrantyType()).append("\n");
        certificate.append("Product: ").append(warranty.getProduct() != null ? warranty.getProduct().getTitle() : "N/A").append("\n");
        certificate.append("Sale ID: ").append(warranty.getSale() != null ? warranty.getSale().getId() : "N/A").append("\n");
        certificate.append("Start Date: ").append(warranty.getStartDate()).append("\n");
        certificate.append("Finish Date: ").append(warranty.getEndDate()).append("\n");
        certificate.append("Additional value $: ").append(warranty.getAdditionalCost()).append("\n");
        return certificate.toString();
    }
}