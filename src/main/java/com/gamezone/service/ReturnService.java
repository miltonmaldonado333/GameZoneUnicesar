package com.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Accessory;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

/**
 * Handles the business logic for managing product returns, including
 * deadline validation, ownership validation, stock restoration for both
 * products and accessories, and monthly balance reporting.
 */
public class ReturnService {

    private ReturnRepository returnRepository;
    private SaleService saleService;
    private ProductService productService;
    private AccessoryService accessoryService;
    private WarrantyService warrantyService;

    /**
     * Constructs a ReturnService with all required dependencies.
     *
     * @param returnRepository  the persistence repository for returns
     * @param saleService       the service managing sales
     * @param productService    the service managing products
     * @param accessoryService  the service managing accessories, used to restore
     *                          stock when a returned item is an accessory
     * @param warrantyService   the service managing warranties, used to cancel
     *                          the warranties of returned consoles
     */
    public ReturnService(ReturnRepository returnRepository, SaleService saleService,
                          ProductService productService, AccessoryService accessoryService,
                          WarrantyService warrantyService) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new return for a set of products belonging to an original sale.
     * Validates sale existence, the 30-day return window, product ownership, and
     * prevents returning more units than were originally purchased.
     * Cancels the warranties of every returned console and adds their refundable
     * cost to the refund.
     *
     * @param saleId     the identifier of the original sale
     * @param productIds the identifiers of the products being returned
     * @param reason     the reason for the return
     * @return the registered Return instance
     * @throws IllegalArgumentException if the sale does not exist, the 30-day
     *                                   window has passed, any product does not
     *                                   belong to the original sale, or the
     *                                   requested quantity exceeds what is
     *                                   available to return
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = findSaleById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("The specified sale does not exist: " + saleId);
        }

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("The sale has exceeded the 30-day return policy window.");
        }

        List<Return> previousReturns = viewReturnsBySale(saleId);

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = findProductInSale(sale, productId);
            if (product == null) {
                throw new IllegalArgumentException("Product " + productId + " does not belong to sale " + saleId);
            }

            long originalQuantity = 0;
            for (Product p : sale.getProducts()) {
                if (p.getId().equalsIgnoreCase(productId)) {
                    originalQuantity++;
                }
            }

            long alreadyReturnedQuantity = 0;
            for (Return prevReturn : previousReturns) {
                for (Product p : prevReturn.getReturnedProducts()) {
                    if (p.getId().equalsIgnoreCase(productId)) {
                        alreadyReturnedQuantity++;
                    }
                }
            }

            long requestedInCurrentBatch = 0;
            for (String id : productIds) {
                if (id.equalsIgnoreCase(productId)) {
                    requestedInCurrentBatch++;
                }
            }

            if (alreadyReturnedQuantity + requestedInCurrentBatch > originalQuantity) {
                throw new IllegalArgumentException("Product " + productId +
                    " has no units available for return in sale " + saleId +
                    " (Purchased: " + originalQuantity + ", Previously returned: " + alreadyReturnedQuantity + ").");
            }

            returnedProducts.add(product);
        }

        String returnId = "RET" + String.format("%04d", returnRepository.loadAll().size() + 1);
        // Cancel the warranties of every returned console and collect the refundable cost
        double warrantyRefund = 0.0;
        for (Product product : returnedProducts) {
            if (product instanceof Console) {
                warrantyRefund += warrantyService.cancelWarranties(product.getId(), saleId);
            }
        }

        Return returnRecord = new Return(returnId, LocalDate.now(), sale, returnedProducts, reason, warrantyRefund);

        // Restore stock, delegating to the appropriate service depending on item type
        for (Product product : returnedProducts) {
            if (product instanceof Accessory) {
                accessoryService.restoreStock(product.getId(), 1);
            } else {
                productService.restoreStock(product.getId(), 1);
            }
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

    /**
     * Returns every return recorded in the system.
     *
     * @return the full list of registered returns
     */
    public List<Return> viewAllReturns() {
        return returnRepository.loadAll();
    }

    /**
     * Returns the returns whose original sale belongs to the given customer.
     *
     * @param customerId the customer identification number
     * @return the list of matching returns
     */
    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> matching = new ArrayList<>();
        for (Return returnRecord : returnRepository.loadAll()) {
            if (returnRecord.getOriginalSale().getClient().getIdentification().equalsIgnoreCase(customerId)) {
                matching.add(returnRecord);
            }
        }
        return matching;
    }

    /**
     * Returns the returns associated with a specific sale.
     *
     * @param saleId the sale identifier
     * @return the list of matching returns
     */
    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> matching = new ArrayList<>();
        for (Return returnRecord : returnRepository.loadAll()) {
            if (String.valueOf(returnRecord.getOriginalSale().getId()).equals(saleId)) {
                matching.add(returnRecord);
            }
        }
        return matching;
    }

    /**
     * Calculates the total sales amount for the given month and year, using
     * each sale's final total (including discounts and extended warranty costs).
     *
     * @param month the month to evaluate (1-12)
     * @param year  the year to evaluate
     * @return the total sales amount for that period
     */
    public double calculateMonthlySales(int month, int year) {
        double totalSales = 0.0;
        for (Sale sale : saleService.getAllSales()) {
            LocalDate saleDate = LocalDate.parse(sale.getDate());
            if (saleDate.getMonthValue() == month && saleDate.getYear() == year) {
                totalSales += sale.getTotal();
            }
        }
        return totalSales;
    }

    /**
     * Calculates the total refunded amount for returns registered in the given
     * month and year.
     *
     * @param month the month to evaluate (1-12)
     * @param year  the year to evaluate
     * @return the total returns amount for that period
     */
    public double calculateMonthlyReturns(int month, int year) {
        double totalReturns = 0.0;
        for (Return returnRecord : returnRepository.loadAll()) {
            LocalDate returnDate = returnRecord.getDate();
            if (returnDate.getMonthValue() == month && returnDate.getYear() == year) {
                totalReturns += returnRecord.getRefundAmount();
            }
        }
        return totalReturns;
    }

    /**
     * Generates the net monthly balance for the given month and year, calculated
     * as the total sales minus the total returns recorded within that period.
     *
     * @param month the month to evaluate (1-12)
     * @param year  the year to evaluate
     * @return the net balance (total sales minus total returns) for that period
     */
    public double generateMonthlyBalance(int month, int year) {
        return calculateMonthlySales(month, year) - calculateMonthlyReturns(month, year);
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
