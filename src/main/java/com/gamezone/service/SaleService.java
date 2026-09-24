package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.List;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

/**
 * Service handling sales transaction business logic, stock validation,
 * automatic inventory updates, and promotion discount evaluation.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final AccessoryService AccessoryService;


    /**
     * Constructs a SaleService with all required dependencies.
     *

     * @param saleRepository   the persistence repository for sales
     * @param productService   the service managing products
     * @param accessoryService the service managing accessories
     * @param promotionService the service evaluating active promotions
     */
    public SaleService(SaleRepository saleRepository, ProductService productService, 
                       AccessoryService accessoryService, PromotionService promotionService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;

     * @param saleRepository The repository used to persist sales.
     * @param productService The service used to manage product inventory.
     * @param AccessoryService the service managing accessories
     */
    public SaleService(SaleRepository saleRepository, ProductService productService, AccessoryService AccessoryService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.AccessoryService = AccessoryService;

    }

    /**
     * Registers a new sale transaction. Evaluates stock availability, deducts inventory,
     * applies the best active promotion, and persists the sale record.
     *
     * @param client   the purchasing customer
     * @param seller   the attending salesperson
     * @param items    the list of products and accessories included in the sale
     * @return the processed and registered Sale instance
     * @throws IllegalArgumentException if items list is empty or stock is insufficient
     */
    public Sale registerSale(Client client, Seller seller, List<Product> items) {
        // Business Rule: A sale must contain at least one item
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product or accessory.");
        }

        // Validate stock for all items prior to transaction processing
        for (Product item : items) {
            if (item.getStock() <= 0) {
                throw new IllegalArgumentException("Insufficient stock for item: " + item.getTitle());
            }
        }

        // Generate unique Sale ID and capture current transaction date
        int nextId = saleRepository.findAll().size() + 1;
        String currentDate = LocalDate.now().toString();

        // Instantiate initial Sale entity
        Sale sale = new Sale(nextId, currentDate, client, seller, items);

        // Evaluate and apply active promotions (Requirement 2)
        if (promotionService != null) {
            Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
            if (bestPromotion != null) {
                double discount = bestPromotion.calculateDiscount(sale);
                if (discount > 0) {
                    sale.setAppliedPromotionName(bestPromotion.getName());
                    sale.setDiscountAmount(discount);
                    // Adjust final total: Subtotal minus Discount
                    sale.setTotal(sale.getSubtotal() - discount);
                }
            }
        }

        // Deduct inventory for sold items
        for (Product item : items) {
            if (item instanceof Accessory) {
                accessoryService.updateStock(item.getId(), item.getStock() - 1);
            } else {
                productService.updateStock(item.getId(), item.getStock() - 1);
            }
        }

        // Persist the completed sale
        saleRepository.save(sale);

        return sale;

  public Sale registerSale(Client client, Seller seller, List<Product> items) {
    if (items == null || items.isEmpty()) {
        throw new IllegalArgumentException("A sale must contain at least one item.");

    }

    // 1. Validar que haya stock suficiente para todos los ítems antes de la transacción
    for (Product item : items) {
        if (item.getStock() < 1) { 
            throw new IllegalArgumentException("Insufficient stock for product: " + item.getTitle());
        }
    }

    // 2. Actualizar el stock restando únicamente la cantidad vendida (por defecto 1 unidad por ítem en la lista)
    int quantitySold = 1; 

    for (Product item : items) {
        if (item instanceof Accessory && AccessoryService != null) {
            // Se pasa la cantidad a restar (1), no el stock restante
            boolean updated = AccessoryService.updateStock(item.getId(), quantitySold);
            if (!updated) {
                throw new IllegalArgumentException("Could not update stock for accessory: " + item.getTitle());
            }
        } else {
            productService.updateStock(item.getId(), quantitySold);
        }
    }
    
    // 3. Generar ID de venta y persistir
    int saleId = saleRepository.findAll().size() + 1;
    String currentDate = LocalDate.now().toString();

    Sale newSale = new Sale(saleId, currentDate, client, seller, items);
    newSale.calculateTotal();

    saleRepository.save(newSale);

    return newSale;
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