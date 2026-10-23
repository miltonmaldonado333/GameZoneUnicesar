package com.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRecord;
import com.gamezone.persistence.WarrantyRepository;

/**
 * Service class responsible for managing the business logic of product
 * warranties. Handles the creation, persistence, replacement, and retrieval of
 * basic and extended warranties.
 */
public class WarrantyService implements WarrantyCanceller {

    private final WarrantyRepository warrantyRepository;
    private final SaleLookup saleLookup;

    /**
     * Constructs a WarrantyService with the abstraction used to look up sales.
     *
     * @param warrantyRepository the persistence repository for warranties
     * @param saleLookup the lookup used to resolve the sales of the stored warranties
     */
    public WarrantyService(WarrantyRepository warrantyRepository, SaleLookup saleLookup) {
        this.warrantyRepository = warrantyRepository;
        this.saleLookup = saleLookup;
    }

    /**
     * Loads the stored warranties and rebuilds each one from its raw record.
     * The sale is resolved through the sale lookup and the product is taken
     * from that sale. A record whose sale or product cannot be found, or whose
     * type is unknown, is skipped.
     *
     * @return the list of rebuilt warranties
     */
    private List<Warranty> loadWarranties() {
        List<Warranty> warranties = new ArrayList<>();
        for (WarrantyRecord stored : warrantyRepository.loadAll()) {
            Sale sale = saleLookup.findSaleById(stored.getSaleId());
            Product product = findProductInSale(sale, stored.getProductId());
            if (sale == null || product == null) {
                continue;
            }

            if ("EXTENDED".equalsIgnoreCase(stored.getType())) {
                warranties.add(new ExtendedWarranty(stored.getId(), product, sale, stored.getStartDate()));
            } else if ("BASIC".equalsIgnoreCase(stored.getType())) {
                warranties.add(new BasicWarranty(stored.getId(), product, sale, stored.getStartDate()));
            }
        }
        return warranties;
    }

    private Product findProductInSale(Sale sale, String productId) {
        if (sale == null) {
            return null;
        }
        for (Product item : sale.getProducts()) {
            if (item.getId().equalsIgnoreCase(productId)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Creates and persists an automatic basic warranty for a given product and
     * sale.
     *
     * @param product the product covered by the warranty
     * @param sale the sale associated with the warranty
     * @param startDate the effective start date of the warranty
     * @return the created and persisted BasicWarranty instance
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        List<Warranty> warranties = loadWarranties();

        String warrantyId = "WAR" + String.format("%04d", warranties.size() + 1);
        BasicWarranty warranty = new BasicWarranty(warrantyId, product, sale, startDate);

        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);

        return warranty;
    }

    /**
     * Creates and persists an extended warranty for a given product and sale.
     * Automatically removes any existing basic warranty for the same product
     * and sale to prevent duplicate coverage records.
     *
     * @param product the product covered by the extended warranty
     * @param sale the sale associated with the warranty
     * @param startDate the effective start date of the warranty
     * @return the created and persisted ExtendedWarranty instance
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        List<Warranty> warranties = loadWarranties();

        warranties.removeIf(w -> w.getProduct().getId().equalsIgnoreCase(product.getId())
                && String.valueOf(w.getSale().getId()).equalsIgnoreCase(String.valueOf(sale.getId())));

        String warrantyId = "WAR" + String.format("%04d", warranties.size() + 1);
        ExtendedWarranty warranty = new ExtendedWarranty(warrantyId, product, sale, startDate);

        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);

        return warranty;
    }

    /**
     * Finds the warranty associated with a specific product within a specific
     * sale.
     *
     * @param productId unique identifier of the product
     * @param saleId unique identifier of the sale
     * @return the matching Warranty instance, or null if no record is found
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        if (productId == null || saleId == null) {
            return null;
        }

        for (Warranty warranty : loadWarranties()) {
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
        return loadWarranties();
    }

    /**
     * Retrieves all warranties that are currently active based on today's date.
     *
     * @return a list of currently active warranties
     */
    public List<Warranty> listActiveWarranties() {
        List<Warranty> active = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Warranty warranty : loadWarranties()) {
            if (warranty.isActive(today)) {
                active.add(warranty);
            }
        }
        return active;
    }

    /**
     * Retrieves all warranties whose expiration dates fall within a specified
     * window of days from today.
     *
     * @param daysAhead number of days in the future to check for upcoming
     * expirations
     * @return a list of warranties expiring within the specified time window
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        List<Warranty> expiringSoon = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);

        for (Warranty warranty : loadWarranties()) {
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
     */
    public void syncPastConsoleWarranties() {
        List<Warranty> currentWarranties = loadWarranties();
        boolean hasChanges = false;

        for (Sale sale : saleLookup.getAllSales()) {
            LocalDate saleDate = LocalDate.parse(sale.getDate());

            for (Product item : sale.getProducts()) {
                if (item.hasWarrantyCoverage()) {
                    boolean alreadyHasWarranty = currentWarranties.stream().anyMatch(w
                            -> w.getProduct().getId().equalsIgnoreCase(item.getId())
                            && w.getSale().getId() == sale.getId()
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

    /**
     * Cancels every warranty attached to the given product within the given
     * sale and returns the amount that can be refunded to the customer for
     * them. A basic warranty is free, so it contributes zero; an extended
     * warranty contributes its additional cost.
     *
     * @param productId unique identifier of the returned product
     * @param saleId unique identifier of the sale the product was bought in
     * @return the refundable warranty cost, or 0.0 if no warranty was found
     */
    @Override
    public double cancelWarranties(String productId, String saleId) {
        if (productId == null || saleId == null) {
            return 0.0;
        }

        List<Warranty> remaining = new ArrayList<>();
        double refundableCost = 0.0;
        boolean removedAny = false;

        for (Warranty warranty : loadWarranties()) {
            boolean matchesProduct = warranty.getProduct().getId().equalsIgnoreCase(productId);
            boolean matchesSale = String.valueOf(warranty.getSale().getId()).equalsIgnoreCase(saleId);

            if (matchesProduct && matchesSale) {
                refundableCost += warranty.getAdditionalCost();
                removedAny = true;
            } else {
                remaining.add(warranty);
            }
        }

        if (removedAny) {
            warrantyRepository.saveAll(remaining);
        }

        return refundableCost;
    }
}