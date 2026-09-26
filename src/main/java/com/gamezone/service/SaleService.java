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
 * assignments.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;

    /**
     * Constructs a SaleService with all required dependencies including
     * WarrantyService.
     *
     * @param saleRepository the persistence repository for sales
     * @param productService the service managing products
     * @param accessoryService the service managing accessories
     * @param promotionService the service evaluating active promotions
     * @param warrantyService the service managing warranties
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
     * Constructs a SaleService without warranty service (supports 4 parameters in Main).
     *
     * @param saleRepository the persistence repository for sales
     * @param productService the service managing products
     * @param accessoryService the service managing accessories
     * @param promotionService the service evaluating active promotions
     */
    public SaleService(SaleRepository saleRepository, ProductService productService,
                        AccessoryService accessoryService, PromotionService promotionService) {
        this(saleRepository, productService, accessoryService, promotionService, null);
    }

    /**
     * Constructs a SaleService without promotion and warranty services (supports 3 parameters).
     *
     * @param saleRepository the persistence repository for sales
     * @param productService the service managing products
     * @param accessoryService the service managing accessories
     */
    public SaleService(SaleRepository saleRepository, ProductService productService, AccessoryService accessoryService) {
        this(saleRepository, productService, accessoryService, null, null);
    }

    /**
     * Overloaded registerSale for backward compatibility (no extended
     * warranties requested).
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
     * Registers a new sale transaction. Evaluates stock availability, deducts
     * inventory, applies warranties, evaluates promotions, and persists the
     * sale record.
     *
     * @param client the purchasing customer
     * @param seller the attending salesperson
     * @param items the list of products and accessories included in the sale
     * @param extendedWarrantyProductIds the list of product IDs requesting
     * extended warranty
     * @return the processed and registered Sale instance
     * @throws IllegalArgumentException if items list is empty or stock is
     * insufficient
     */
    public Sale registerSale(Client client, Seller seller, List<Product> items, List<String> extendedWarrantyProductIds) {
        // Business Rule: A sale must contain at least one item
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product or accessory.");
        }

        // Validate stock for all items prior to transaction processing
        for (Product item : items) {
            if (item.getStock() < 1) {
                throw new IllegalArgumentException("Insufficient stock for item: " + item.getTitle());
            }
        }

        // Deduct inventory for sold items
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

        // Generate unique Sale ID and capture current transaction date
        int nextId = saleRepository.findAll().size() + 1;
        LocalDate saleDate = LocalDate.now();
        String currentDate = saleDate.toString();

        // Instantiate initial Sale entity
        Sale sale = new Sale(nextId, currentDate, client, seller, items);
        sale.calculateTotal();

        // Evaluate and apply active promotions (Requirement 2)
        if (promotionService != null) {
            Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
            if (bestPromotion != null) {
                double discount = bestPromotion.calculateDiscount(sale);
                if (discount > 0) {
                    sale.setAppliedPromotionName(bestPromotion.getName());
                    sale.setDiscountAmount(discount);
                    sale.setTotal(sale.getSubtotal() - discount);
                }
            }
        }

        // Process warranties (Requirement 4)
        if (warrantyService != null) {
            double extraWarrantyCost = 0.0;

            // Extended warranty IDs ki editable copy banayi gayi hai
            List<String> remainingExtendedIds = (extendedWarrantyProductIds != null) 
                    ? new ArrayList<>(extendedWarrantyProductIds) 
                    : new ArrayList<>();

            for (Product item : items) {
                if (item instanceof Console) {
                    // Check ki kya is console ID ko extended warranty chahiye
                    boolean wantsExtended = remainingExtendedIds.contains(item.getId());

                    if (wantsExtended) {
                        // Extended warranty assign karein aur 10% cost add karein
                        var warranty = warrantyService.assignExtendedWarranty(item, sale, saleDate);
                        if (warranty != null) {
                            extraWarrantyCost += warranty.getAdditionalCost();
                        }
                        // ID ko remove karein taaki doosra same product basic warranty le sake
                        remainingExtendedIds.remove(item.getId());
                    } else {
                        // Automatic basic warranty assign karein console ke liye
                        warrantyService.assignBasicWarranty(item, sale, saleDate);
                    }
                }
            }

            // Extended warranty ke total cost ko final total mein add karein
            if (extraWarrantyCost > 0) {
                sale.setTotal(sale.getTotal() + extraWarrantyCost);
            }
        }

        // Persist the completed sale
        saleRepository.save(sale);

        return sale;
    }

    /**
     * Retrieves all recorded sales in the system.
     *
     * @return list of all sales
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Retrieves sales associated with a specific client identification.
     *
     * @param clientId the customer identification
     * @return list of sales matching the client
     */
    public List<Sale> getSalesByClient(String clientId) {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getClient().getIdentification().equalsIgnoreCase(clientId))
                .toList();
    }

    /**
     * Retrieves sales attended by a specific seller employee code.
     *
     * @param sellerCode the seller employee code
     * @return list of sales matching the seller
     */
    public List<Sale> getSalesBySeller(String sellerCode) {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getSeller().getEmployeeCode().equalsIgnoreCase(sellerCode))
                .toList();
    }
}