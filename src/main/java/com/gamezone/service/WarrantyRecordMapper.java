package com.gamezone.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRecord;
import com.gamezone.persistence.WarrantyTypeRegistry;

/**
 * Rebuilds the stored warranties from their raw records. The sale is resolved
 * through a narrow abstraction and the product is taken from that sale. The
 * subtype of each warranty is created by the creator registered for its stored
 * type, so a new warranty type is added by registering a creator and this class
 * does not change.
 */
public class WarrantyRecordMapper {

    private final SaleLookup saleLookup;
    private final Map<String, WarrantyCreator> creators;

    /**
     * Creates a mapper that resolves the sales through the given lookup and uses
     * the default creators.
     *
     * @param saleLookup the lookup used to resolve the sales of the stored warranties
     */
    public WarrantyRecordMapper(SaleLookup saleLookup) {
        this(saleLookup, defaultCreators());
    }

    /**
     * Creates a mapper that resolves the sales through the given lookup and uses
     * the given creators.
     *
     * @param saleLookup the lookup used to resolve the sales of the stored warranties
     * @param creators   the creator of each stored type, keyed by the type in upper case
     */
    public WarrantyRecordMapper(SaleLookup saleLookup, Map<String, WarrantyCreator> creators) {
        this.saleLookup = saleLookup;
        this.creators = creators;
    }

    private static Map<String, WarrantyCreator> defaultCreators() {
        Map<String, WarrantyCreator> creators = new LinkedHashMap<>();
        creators.put(WarrantyTypeRegistry.EXTENDED, ExtendedWarranty::new);
        creators.put(WarrantyTypeRegistry.BASIC, BasicWarranty::new);
        return creators;
    }

    /**
     * Rebuilds a warranty from each raw record. The sale is resolved through the
     * sale lookup and the product is taken from that sale. A record whose sale or
     * product cannot be found, or whose type has no creator, is skipped.
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

            WarrantyCreator creator = creatorFor(stored.getType());
            if (creator != null) {
                warranties.add(creator.create(stored.getId(), product, sale, stored.getStartDate()));
            }
        }
        return warranties;
    }

    private WarrantyCreator creatorFor(String type) {
        if (type == null) {
            return null;
        }
        return creators.get(type.toUpperCase(Locale.ROOT));
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