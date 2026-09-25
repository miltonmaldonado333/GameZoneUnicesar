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
}
