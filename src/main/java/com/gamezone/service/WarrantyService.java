package com.gamezone.service;

import java.time.LocalDate;
import java.util.List;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;

/**
 * Handles the business logic for assigning and querying product warranties.
 */
public class WarrantyService {

    private WarrantyRepository warrantyRepository;

    public WarrantyService(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }

    /**
     * Creates and persists an automatic basic warranty for the given product and sale.
     *
     * @param product   the product covered by the warranty
     * @param sale      the sale that generated the warranty
     * @param startDate the date the warranty starts
     * @return the created BasicWarranty
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        String warrantyId = "WAR" + String.format("%04d", warrantyRepository.loadAll().size() + 1);
        BasicWarranty warranty = new BasicWarranty(warrantyId, product, sale, startDate);

        List<Warranty> warranties = warrantyRepository.loadAll();
        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);

        return warranty;
    }

    /**
     * Creates and persists an extended warranty for the given product and sale.
     *
     * @param product   the product covered by the warranty
     * @param sale      the sale that generated the warranty
     * @param startDate the date the warranty starts
     * @return the created ExtendedWarranty
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        String warrantyId = "WAR" + String.format("%04d", warrantyRepository.loadAll().size() + 1);
        ExtendedWarranty warranty = new ExtendedWarranty(warrantyId, product, sale, startDate);

        List<Warranty> warranties = warrantyRepository.loadAll();
        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);

        return warranty;
    }
    /**
     * Finds the warranty associated with a specific product within a specific sale.
     *
     * @param productId the product identifier
     * @param saleId    the sale identifier
     * @return the matching warranty, or null if none is found
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty warranty : warrantyRepository.loadAll()) {
            boolean matchesProduct = warranty.getProduct().getId().equalsIgnoreCase(productId);
            boolean matchesSale = String.valueOf(warranty.getSale().getId()).equals(saleId);
            if (matchesProduct && matchesSale) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * Returns every warranty recorded in the system.
     *
     * @return the full list of registered warranties
     */
    public List<Warranty> listAllWarranties() {
        return warrantyRepository.loadAll();
    }
}
