package com.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;

/**
 * Service class responsible for managing the business logic of product warranties.
 * Handles the creation, persistence, replacement, and retrieval of basic and extended warranties.
 * Depends on SaleRepository (rather than SaleService) to avoid a circular dependency,
 * since SaleService itself depends on WarrantyService.
 */
public class WarrantyService {

    private WarrantyRepository warrantyRepository;
    private SaleRepository saleRepository;
    private ProductService productService;

    /**
     * Constructs a new WarrantyService with its required dependencies.
     *
     * @param warrantyRepository the persistence repository for warranties
     * @param saleRepository     the persistence repository for sales, used for historical sync
     * @param productService     the service managing products
     */
    public WarrantyService(WarrantyRepository warrantyRepository, SaleRepository saleRepository, ProductService productService) {
        this.warrantyRepository = warrantyRepository;
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    /**
     * Creates and persists an automatic basic warranty for a given product and sale.
     *
     * @param product   the product covered by the warranty
     * @param sale      the sale associated with the warranty
     * @param startDate the effective start date of the warranty
     * @return the created and persisted BasicWarranty instance
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        List<Warranty> warranties = warrantyRepository.loadAll();

        String warrantyId = "WAR" + String.format("%04d", warranties.size() + 1);
        BasicWarranty warranty = new BasicWarranty(warrantyId, product, sale, startDate);

        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);

        return warranty;
    }

    /**
     * Creates and persists an extended warranty for a given product and sale.
     * Automatically removes any existing basic warranty for the same product and sale
     * to prevent duplicate coverage records.
     *
     * @param product   the product covered by the extended warranty
     * @param sale      the sale associated with the warranty
     * @param startDate the effective start date of the warranty
     * @return the created and persisted ExtendedWarranty instance
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        List<Warranty> warranties = warrantyRepository.loadAll();

        warranties.removeIf(w -> w.getProduct().getId().equalsIgnoreCase(product.getId())
                              && String.valueOf(w.getSale().getId()).equalsIgnoreCase(String.valueOf(sale.getId())));

        String warrantyId = "WAR" + String.format("%04d", warranties.size() + 1);
        ExtendedWarranty warranty = new ExtendedWarranty(warrantyId, product, sale, startDate);

        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);

        return warranty;
    }

    /**
     * Finds the warranty associated with a specific product within a specific sale.
     *
     * @param productId unique identifier of the product
     * @param saleId    unique identifier of the sale
     * @return the matching Warranty instance, or null if no record is found
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        if (productId == null || saleId == null) {
            return null;
        }

        for (Warranty warranty : warrantyRepository.loadAll()) {
            boolean matchesProduct = warranty.getProduct().getId().equalsIgnoreCase(productId);
            boolean matchesSale = String.valueOf(warranty.getSale().getId()).equalsIgnoreCase(saleId);
            if (matchesProduct && matchesSale) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * Retrieves all recorded warranties from the system.
     *
     * @return a list containing all registered warranties
     */
    public List<Warranty> listAllWarranties() {
        return warrantyRepository.loadAll();
    }

    /**
     * Retrieves all warranties that are currently active based on today's date.
     *
     * @return a list of currently active warranties
     */
    public List<Warranty> listActiveWarranties() {
        List<Warranty> active = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Warranty warranty : warrantyRepository.loadAll()) {
            if (warranty.isActive(today)) {
                active.add(warranty);
            }
        }
        return active;
    }

    /**
     * Retrieves all warranties whose expiration dates fall within a specified window of days from today.
     *
     * @param daysAhead number of days in the future to check for upcoming expirations
     * @return a list of warranties expiring within the specified time window
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        List<Warranty> expiringSoon = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);

        for (Warranty warranty : warrantyRepository.loadAll()) {
            LocalDate endDate = warranty.getEndDate();
            boolean withinWindow = !endDate.isBefore(today) && !endDate.isAfter(limit);
            if (withinWindow) {
                expiringSoon.add(warranty);
            }
        }

        return expiringSoon;
    }

    /**
     * Scans all historical sales and automatically generates a basic warranty
     * for any console product that does not already have a warranty assigned.
     * Uses the injected SaleRepository directly instead of receiving SaleService
     * as a parameter, since SaleService is not available at this point in
     * construction without causing a circular dependency.
     */
    public void syncPastConsoleWarranties() {
        if (saleRepository == null) {
            return;
        }

        List<Warranty> currentWarranties = warrantyRepository.loadAll();
        boolean hasChanges = false;

        for (Sale sale : saleRepository.findAll()) {
            LocalDate saleDate = LocalDate.parse(sale.getDate());

            for (Product item : sale.getProducts()) {
                if (item instanceof com.gamezone.model.Console) {
                    boolean alreadyHasWarranty = currentWarranties.stream().anyMatch(w ->
                        w.getProduct().getId().equalsIgnoreCase(item.getId()) &&
                        String.valueOf(w.getSale().getId()).equalsIgnoreCase(String.valueOf(sale.getId()))
                    );

                    if (!alreadyHasWarranty) {
                        String warrantyId = "WAR" + String.format("%04d", currentWarranties.size() + 1);
                        BasicWarranty basicWarranty = new BasicWarranty(warrantyId, item, sale, saleDate);
                        currentWarranties.add(basicWarranty);
                        hasChanges = true;
                    }
                }
            }
        }

        if (hasChanges) {
            warrantyRepository.saveAll(currentWarranties);
        }
    }
}
