package com.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.validation.ReturnValidator;

/**
 * Handles the business logic for managing product returns, including
 * deadline validation, ownership validation, stock restoration and warranty
 * cancellation. It depends only on abstractions; rebuilding the stored returns
 * and computing the monthly balance are delegated to helper classes.
 */
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final SaleLookup saleLookup;
    private final WarrantyCanceller warrantyCanceller;
    private final ItemStock stockService;
    private final ReturnRecordMapper recordMapper;
    private final MonthlyBalanceCalculator balanceCalculator;

    /**
     * Constructs a ReturnService with the abstractions it needs.
     *
     * @param returnRepository  the persistence repository for returns
     * @param saleLookup        the lookup used to find the original sale
     * @param monthlySales      the source of the monthly sales total
     * @param productService    the service used to resolve the returned products
     * @param accessoryLookup   the lookup used to resolve the returned accessories
     * @param warrantyCanceller the abstraction used to cancel the warranties of returned consoles
     * @param stockService      the stock contract used to restore the stock of the returned items
     */
    public ReturnService(ReturnRepository returnRepository, SaleLookup saleLookup,
                          MonthlySales monthlySales, IProductService productService,
                          AccessoryLookup accessoryLookup, WarrantyCanceller warrantyCanceller,
                          ItemStock stockService) {
        this.returnRepository = returnRepository;
        this.saleLookup = saleLookup;
        this.warrantyCanceller = warrantyCanceller;
        this.stockService = stockService;
        this.recordMapper = new ReturnRecordMapper(saleLookup, productService, accessoryLookup);
        this.balanceCalculator = new MonthlyBalanceCalculator(monthlySales);
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
        Sale sale = saleLookup.findSaleById(saleId);
        ReturnValidator.validateReturnableSale(sale, saleId);

        List<Return> previousReturns = viewReturnsBySale(saleId);

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = findProductInSale(sale, productId);
            ReturnValidator.validateProductBelongsToSale(product, productId, saleId);
            ReturnValidator.validateReturnableQuantity(sale, previousReturns, productIds, productId, saleId);
            returnedProducts.add(product);
        }

        String returnId = "RET" + String.format("%04d", loadReturns().size() + 1);
        // Cancel the warranties of every returned console and collect the refundable cost
        double warrantyRefund = 0.0;
        for (Product product : returnedProducts) {
            if (product.hasWarrantyCoverage()) {
                warrantyRefund += warrantyCanceller.cancelWarranties(product.getId(), saleId);
            }
        }

        Return returnRecord = new Return(returnId, LocalDate.now(), sale, returnedProducts, reason, warrantyRefund);

        // Restore stock through the common stock contract
        for (Product product : returnedProducts) {
            stockService.restoreStock(product, 1);
        }

        List<Return> returns = loadReturns();
        returns.add(returnRecord);
        returnRepository.saveAll(returns);

        return returnRecord;
    }

    private List<Return> loadReturns() {
        return recordMapper.toReturns(returnRepository.loadAll());
    }

    /**
     * Returns every return recorded in the system.
     *
     * @return the full list of registered returns
     */
    public List<Return> viewAllReturns() {
        return loadReturns();
    }

    /**
     * Returns the returns whose original sale belongs to the given customer.
     *
     * @param customerId the customer identification number
     * @return the list of matching returns
     */
    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> matching = new ArrayList<>();
        for (Return returnRecord : loadReturns()) {
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
        for (Return returnRecord : loadReturns()) {
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
        return balanceCalculator.calculateMonthlySales(month, year);
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
        return balanceCalculator.calculateMonthlyReturns(loadReturns(), month, year);
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
        return balanceCalculator.generateMonthlyBalance(loadReturns(), month, year);
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