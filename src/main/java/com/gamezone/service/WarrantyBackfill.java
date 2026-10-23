package com.gamezone.service;

import java.time.LocalDate;
import java.util.List;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;

/**
 * Generates the basic warranties that the historical sales are missing. The
 * sales are read through a narrow abstraction, so this class does not depend on
 * the sale service.
 */
public class WarrantyBackfill {

    private final SaleLookup saleLookup;

    /**
     * Creates a backfill that reads the sales through the given lookup.
     *
     * @param saleLookup the lookup used to read the historical sales
     */
    public WarrantyBackfill(SaleLookup saleLookup) {
        this.saleLookup = saleLookup;
    }

    /**
     * Scans all historical sales and adds a basic warranty to the given list for
     * every covered item that does not already have one.
     *
     * @param currentWarranties the warranties that already exist; new ones are added to this list
     * @return true if at least one warranty was added
     */
    public boolean addMissingBasicWarranties(List<Warranty> currentWarranties) {
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

        return hasChanges;
    }
}