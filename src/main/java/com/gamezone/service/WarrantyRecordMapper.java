package com.gamezone.service;

import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRecord;

/**
 * Rebuilds the stored warranties from their raw records. The sale is resolved
 * through a narrow abstraction and the product is taken from that sale, so this
 * class knows nothing about the services that own them.
 */
public class WarrantyRecordMapper {

    private final SaleLookup saleLookup;

    /**
     * Creates a mapper that resolves the sales through the given lookup.
     *
     * @param saleLookup the lookup used to resolve the sales of the stored warranties
     */
    public WarrantyRecordMapper(SaleLookup saleLookup) {
        this.saleLookup = saleLookup;
    }

    /**
     * Rebuilds a warranty from each raw record. The sale is resolved through the
     * sale lookup and the product is taken from that sale. A record whose sale or
     * product cannot be found, or whose type is unknown, is skipped.
     *
     * @param records the raw records read from the warranties file
     * @return the list of rebuilt warranties
     */
    public List<Warranty> toWarranties(List<WarrantyRecord> records) {
        List<Warranty> warranties = new ArrayList<>();
        for (WarrantyRecord stored : records) {
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
}