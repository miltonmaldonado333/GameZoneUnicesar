package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling sales transaction business logic, stock validation,
 * automatic inventory updates, promotion discount evaluation, and warranty
 * assignments following the unified sale registration flow (A3 adjustment).
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;

    /**
     * Constructs a SaleService instance with all required dependencies including WarrantyService.
     *
     * @param saleRepository the persistence repository for managing sales records
     * @param productService the service managing product catalog operations
     * @param accessoryService the service managing accessory inventory operations
     * @param promotionService the service evaluating active promotions and discounts
     * @param warrantyService the service managing basic and extended warranties
     */
    public SaleService(SaleRepository saleRepository, ProductService productService,
                        AccessoryService accessoryService, PromotionService promotionService,
                        WarrantyService warrantyService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Constructs a SaleService instance without warranty service support.
     *
     * @param saleRepository the persistence repository for managing sales records
     * @param productService the service managing product catalog operations
     * @param accessoryService the service managing accessory inventory operations
     * @param promotionService the service evaluating active promotions and discounts
     */
    public SaleService(SaleRepository saleRepository, ProductService productService,
                        AccessoryService accessoryService, PromotionService promotionService) {
        this(saleRepository, productService, accessoryService, promotionService, null);
    }

    /**
     * Constructs a SaleService instance without promotion and warranty services support.
     *
     * @param saleRepository the persistence repository for managing sales records
     * @param productService the service managing product catalog operations
     * @param accessoryService the service managing accessory inventory operations
     */
    public SaleService(SaleRepository saleRepository, ProductService productService, AccessoryService accessoryService) {
        this(saleRepository, productService, accessoryService, null, null);
    }

    /**
     * Overloaded registerSale method for backward compatibility when no extended
     * warranties are requested.
     *
     * @param client the purchasing customer
     * @param seller the attending salesperson
     * @param items the list of products and accessories included in the sale
     * @return the processed and registered Sale instance
     */
    public Sale registerSale(Client client, Seller seller, List<Product> items) {
        return registerSale(client, seller, items, null);
    }

    /**
     * Registers a new unified sale transaction according to the A3 integration sequence:
     * <ol>
     *   <li>Validates that the sale contains at least one item.</li>
     *   <li>Resolves each item as product or accessory and validates stock availability.</li>
     *   <li>Creates the sale instance and calculates the initial subtotal.</li>
     *   <li>Queries PromotionService for the best promotion and applies discount strictly on subtotal.</li>
     *   <li>Generates basic warranty for each console and requested extended warranties, summing additional costs.</li>
     *   <li>Calculates the final total: subtotal - discount + extended warranty costs.</li>
     *   <li>Updates inventory delegating to ProductService or AccessoryService according to item type.</li>
     *   <li>Persists the sale and warranty records.</li>
     * </ol>
     *
     * @param client the purchasing customer
     * @param seller the attending salesperson
     * @param items the list of products and accessories included in the sale
     * @param extendedWarrantyProductIds the list of console product IDs requesting extended warranty
     * @return the processed and registered Sale instance
     * @throws IllegalArgumentException if the items list is null/empty or if any item has insufficient stock
     */
    public Sale registerSale(Client client, Seller seller, List<Product> items, List<String> extendedWarrantyProductIds) {
        
        // Step 1: Validate that the sale has at least one item
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product or accessory.");
        }

        // Step 2: Resolve each item as product or accessory and validate stock
        for (Product item : items) {
            if (item.getStock() < 1) {
                throw new IllegalArgumentException("Insufficient stock for item: " + item.getTitle());
            }
        }

        // Step 3: Create the sale and calculate the subtotal
        int nextId = saleRepository.findAll().size() + 1;
        LocalDate saleDate = LocalDate.now();
        String currentDate = saleDate.toString();

        Sale sale = new Sale(nextId, currentDate, client, seller, items);
        sale.calculateTotal(); // Computes initial subtotal from item list

        // Step 4: Consult PromotionService for best promotion and apply discount strictly on subtotal
        double discountAmount = 0.0;
        if (promotionService != null) {
            Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
            if (bestPromotion != null) {
                discountAmount = bestPromotion.calculateDiscount(sale);
                if (discountAmount > 0) {
                    sale.setAppliedPromotionName(bestPromotion.getName());
                    sale.setDiscountAmount(discountAmount);
                }
            }
        }

        // Step 5: Generate basic warranty for each console and requested extended warranties, summing costs
        double totalExtendedWarrantyCost = 0.0;
        if (warrantyService != null) {
            List<String> remainingExtendedIds = (extendedWarrantyProductIds != null) 
                    ? new ArrayList<>(extendedWarrantyProductIds) 
                    : new ArrayList<>();

            for (Product item : items) {
                if (item instanceof Console) {
                    boolean wantsExtended = remainingExtendedIds.contains(item.getId());

                    if (wantsExtended) {
                        // Assign extended warranty and accumulate additional cost
                        var warranty = warrantyService.assignExtendedWarranty(item, sale, saleDate);
                        if (warranty != null) {
                            totalExtendedWarrantyCost += warranty.getAdditionalCost();
                        }
                        remainingExtendedIds.remove(item.getId());
                    } else {
                        // Assign automatic basic warranty for console
                        warrantyService.assignBasicWarranty(item, sale, saleDate);
                    }
                }
            }
        }

        // Step 6: Calculate final total: subtotal - discount + extended warranty costs
        double subtotal = sale.getSubtotal();
        double finalTotal = subtotal - discountAmount + totalExtendedWarrantyCost;
        sale.setTotal(finalTotal);

        // Step 7: Update inventory delegating to ProductService or AccessoryService according to item type
        int quantitySold = 1;
        for (Product item : items) {
            if (item instanceof Accessory && accessoryService != null) {
                boolean updated = accessoryService.updateStock(item.getId(), quantitySold);
                if (!updated) {
                    throw new IllegalArgumentException("Could not update stock for accessory: " + item.getTitle());
                }
            } else if (productService != null) {
                productService.updateStock(item.getId(), quantitySold);
            }
        }

        // Step 8: Persist the sale and warranties
        saleRepository.save(sale);

        return sale;
    }

    /**
     * Retrieves all recorded sales in the system.
     *
     * @return a list containing all recorded sales
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Retrieves sales associated with a specific client identification.
     *
     * @param clientId the customer identification number
     * @return a list of sales matching the given client identification
     */
    public List<Sale> getSalesByClient(String clientId) {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getClient().getIdentification().equalsIgnoreCase(clientId))
                .toList();
    }

    /**
     * Retrieves sales attended by a specific seller employee code.
     *
     * @param sellerCode the employee code of the seller
     * @return a list of sales matching the given seller code
     */
    public List<Sale> getSalesBySeller(String sellerCode) {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getSeller().getEmployeeCode().equalsIgnoreCase(sellerCode))
                .toList();
    }
}