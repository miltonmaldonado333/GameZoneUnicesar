package com.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

/**
 * Handles the business logic for managing product returns, including
 * deadline validation, ownership validation, stock restoration, and
 * monthly balance reporting.
 */
public class ReturnService {

    private ReturnRepository returnRepository;
    private SaleService saleService;
    private ProductService productService;

    public ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * Registers a new return for a set of products belonging to an original sale.
     *
     * @param saleId     the identifier of the original sale
     * @param productIds the identifiers of the products being returned
     * @param reason     the reason for the return
     * @return the registered Return instance
     * @throws IllegalArgumentException if the sale does not exist, the 30-day
     *                                   window has passed, or any product does
     *                                   not belong to the original sale
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = findSaleById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("La venta indicada no existe: " + saleId);
        }

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("La venta ya supero el plazo de 30 dias para devoluciones.");
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = findProductInSale(sale, productId);
            if (product == null) {
                throw new IllegalArgumentException("El producto " + productId + " no pertenece a la venta indicada.");
            }
            returnedProducts.add(product);
        }

        String returnId = "RET" + String.format("%04d", returnRepository.loadAll().size() + 1);
        Return returnRecord = new Return(returnId, LocalDate.now(), sale, returnedProducts, reason);

        for (Product product : returnedProducts) {
            productService.restoreStock(product.getId(), 1);
        }

        List<Return> returns = returnRepository.loadAll();
        returns.add(returnRecord);
        returnRepository.saveAll(returns);

        return returnRecord;
    }

    private Sale findSaleById(String saleId) {
        for (Sale sale : saleService.getAllSales()) {
            if (String.valueOf(sale.getId()).equals(saleId)) {
                return sale;
            }
        }
        return null;
    }

    private Product findProductInSale(Sale sale, String productId) {
        for (Product product : sale.getProducts()) {
            if (product.getId().equalsIgnoreCase(productId)) {
                return product;
            }
        }
        return null;
    }
}
