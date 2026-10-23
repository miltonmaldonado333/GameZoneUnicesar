package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.ISaleRepository;
import com.gamezone.validation.ProductValidator;
import com.gamezone.validation.SaleValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling sales transaction business logic, stock validation,
 * promotion discounts, warranty assignments, and sales queries.
 * Adheres to Dependency Inversion Principle (DIP) by depending on ISaleRepository interface.
 */
public class SaleService {

    /** Repository for sale persistence operations abstraction. */
    private final ISaleRepository saleRepository;
    /** Service contract for stock and inventory validation/updates. */
    private final IStockService stockService;
    /** Service for evaluating active promotions and discounts. */
    private final PromotionService promotionService;
    /** Service for managing basic and extended warranties. */
    private final WarrantyService warrantyService;
    /** Service for looking up clients and sellers. */
    private final PersonService personService;
    /** Service for looking up product catalog entities. */
    private final ProductService productService;

    /**
     * Constructs a SaleService instance with all required abstractions and dependencies.
     *
     * @param saleRepository   the repository interface for managing sales records
     * @param stockService      the service contract managing stock operations
     * @param promotionService the service evaluating promotions and discounts
     * @param warrantyService  the service managing product warranties
     * @param personService    the service looking up clients and sellers
     * @param productService  the service looking up products
     */
    public SaleService(ISaleRepository saleRepository,
                       IStockService stockService,
                       PromotionService promotionService,
                       WarrantyService warrantyService,
                       PersonService personService,
                       ProductService productService) {
        this.saleRepository = saleRepository;
        this.stockService = stockService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
        this.personService = personService;
        this.productService = productService;
    }

    /**
     * Overloaded registerSale method for transactions without extended warranties.
     *
     * @param client the purchasing customer
     * @param seller the attending salesperson
     * @param items  the list of products included in the sale
     * @return the processed and registered Sale instance
     */
    public Sale registerSale(Client client, Seller seller, List<Product> items) {
        return registerSale(client, seller, items, null);
    }

    /**
     * Registers a new sale transaction following business validation, stock checks,
     * promotion discounts, warranty assignments, stock reduction, and persistence.
     *
     * @param client                     the purchasing customer
     * @param seller                     the attending salesperson
     * @param items                      the list of products included in the sale
     * @param extendedWarrantyProductIds the list of product IDs requesting extended warranty
     * @return the processed and registered Sale instance
     */
    public Sale registerSale(Client client, Seller seller, List<Product> items, List<String> extendedWarrantyProductIds) {

        // Step 1: Input data validation
        SaleValidator.validateSaleData(client, seller, items);

        // Step 2: Stock availability validation
        for (Product item : items) {
            ProductValidator.validateStockAvailability(item, 1);
        }

        // Step 3: Sale creation and initial subtotal calculation
        int nextId = getAllSales().size() + 1;
        LocalDate saleDate = LocalDate.now();
        Sale sale = new Sale(nextId, saleDate.toString(), client, seller, items);
        sale.calculateTotal();

        // Step 4: Promotions and discount evaluation
        double discountAmount = 0.0;
        if (promotionService != null) {
            Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
            if (bestPromotion != null) {
                discountAmount = bestPromotion.calculateDiscount(sale);
                if (discountAmount > 0) {
                    sale.setAppliedPromotionName(bestPromotion.getName());
                }
            }
        }
        sale.setDiscountAmount(discountAmount);

        // Step 5: Polymorphic warranty management
        double totalExtendedWarrantyCost = 0.0;
        if (warrantyService != null) {
            List<String> remainingExtendedIds = (extendedWarrantyProductIds != null)
                    ? new ArrayList<>(extendedWarrantyProductIds)
                    : new ArrayList<>();

            for (Product item : items) {
                if (item.hasWarrantyCoverage()) {
                    boolean wantsExtended = remainingExtendedIds.contains(String.valueOf(item.getId()));
                    if (wantsExtended) {
                        var warranty = warrantyService.assignExtendedWarranty(item, sale, saleDate);
                        if (warranty != null) {
                            totalExtendedWarrantyCost += warranty.getAdditionalCost();
                        }
                        remainingExtendedIds.remove(String.valueOf(item.getId()));
                    } else {
                        warrantyService.assignBasicWarranty(item, sale, saleDate);
                    }
                }
            }
        }

        sale.setExtendedWarrantyCost(totalExtendedWarrantyCost);

        // Step 6: Final total calculation
        double subtotal = sale.getSubtotal();
        double finalTotal = subtotal - discountAmount + totalExtendedWarrantyCost;
        sale.setTotal(finalTotal);

        // Step 7: Unified stock update
        for (Product item : items) {
            stockService.reduceStock(String.valueOf(item.getId()), 1);
        }

        // Step 8: Sale persistence via abstraction
        saleRepository.save(sale);

        return sale;
    }

    /**
     * Retrieves all sales by delegating to the repository or parsing raw lines
     * with backward compatibility.
     *
     * @return a list of fully populated Sale objects
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Calculates the total sum of sales registered for a specific month and year.
     *
     * @param month the month to evaluate (1-12)
     * @param year  the year to evaluate
     * @return the sum of sales totals for the specified month and year
     */
    public double calculateMonthlyTotal(int month, int year) {
        double total = 0.0;
        for (Sale sale : getAllSales()) {
            LocalDate saleDate = LocalDate.parse(sale.getDate());
            if (saleDate.getMonthValue() == month && saleDate.getYear() == year) {
                total += sale.getTotal();
            }
        }
        return total;
    }

    /**
     * Finds a recorded sale by its unique identifier string.
     *
     * @param saleId the unique identifier of the sale
     * @return the matching sale instance, or null if not found
     */
    public Sale findSaleById(String saleId) {
        return getAllSales().stream()
                .filter(sale -> String.valueOf(sale.getId()).equalsIgnoreCase(saleId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all sales associated with a specific client identification.
     *
     * @param clientId the customer identification number
     * @return a list of sales matching the given client identification
     */
    public List<Sale> getSalesByClient(String clientId) {
        return getAllSales().stream()
                .filter(Sale -> Sale.getClient().getIdentification().equalsIgnoreCase(clientId)
                ).toList();
    }

    /**
     * Retrieves all sales handled by a specific seller employee code.
     *
     * @param sellerCode the employee code of the seller
     * @return a list of sales matching the given seller code
     */
    public List<Sale> getSalesBySeller(String sellerCode) {
        return getAllSales().stream()
                .filter(Sale -> Sale.getSeller().getIdentification().equalsIgnoreCase(sellerCode)
                ).toList();
    }
}