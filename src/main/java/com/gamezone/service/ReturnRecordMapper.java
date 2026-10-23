package com.gamezone.service;

import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRecord;

/**
 * Rebuilds the stored returns from their raw records. The sale, the products and
 * the accessories are resolved through narrow abstractions, so this class knows
 * nothing about the services that own them.
 */
public class ReturnRecordMapper {

    private final SaleLookup saleLookup;
    private final IProductService productService;
    private final AccessoryLookup accessoryLookup;

    /**
     * Creates a mapper over the lookups it needs to resolve the stored identifiers.
     *
     * @param saleLookup      the lookup used to resolve the original sale
     * @param productService  the service used to resolve the returned products
     * @param accessoryLookup the lookup used to resolve the returned accessories
     */
    public ReturnRecordMapper(SaleLookup saleLookup, IProductService productService,
            AccessoryLookup accessoryLookup) {
        this.saleLookup = saleLookup;
        this.productService = productService;
        this.accessoryLookup = accessoryLookup;
    }

    /**
     * Rebuilds a return from each raw record. Each item is looked up among the
     * products first and then among the accessories. A record whose sale cannot
     * be found is skipped.
     *
     * @param records the raw records read from the returns file
     * @return the list of rebuilt returns
     */
    public List<Return> toReturns(List<ReturnRecord> records) {
        List<Return> returns = new ArrayList<>();
        for (ReturnRecord stored : records) {
            Sale sale = saleLookup.findSaleById(stored.getSaleId());
            if (sale == null) {
                continue;
            }

            List<Product> products = new ArrayList<>();
            for (String productId : stored.getProductIds()) {
                Product product = productService.findProductById(productId);
                if (product == null) {
                    product = accessoryLookup.findById(productId);
                }
                if (product != null) {
                    products.add(product);
                }
            }

            returns.add(new Return(stored.getReturnId(), stored.getDate(), sale, products,
                    stored.getReason(), stored.getWarrantyRefundAmount()));
        }
        return returns;
    }
}