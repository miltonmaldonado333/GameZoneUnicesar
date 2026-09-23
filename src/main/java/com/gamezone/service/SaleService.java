package com.gamezone.service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

/**
 * Service handling sale transactions, stock validation, and inventory
 * delegation. Integrated to handle both standard products and accessories.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccessoryService AccessoryService;

    /**
     * Constructs a SaleService with the required dependencies.
     *
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
     * Registers a new sale transaction in the system. Validates stock
     * availability, updates inventory across appropriate services, and persists
     * the transaction.
     *
     * @param client the customer making the purchase
     * @param seller the employee handling the transaction
     * @param items the list of products/accessories being purchased
     * @return the created Sale instance
     * @throws IllegalArgumentException if items list is empty or stock is
     * insufficient
     */
    public Sale registerSale(Client client, Seller seller, List<Product> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one item.");
        }

        // 1. Validate stock for all items prior to transaction
        for (Product item : items) {
            if (item.getStock()< 1) { // Cambiar a getStock() si tu atributo en Product es stock
                throw new IllegalArgumentException("Insufficient stock for product: " + item.getTitle());
            }
        }

        // 2. Update stock using productService while AccessoryService is integrated
        for (Product item : items) {
            int newQuantity = item.getStock()- 1;
            if (item instanceof Accessory && AccessoryService != null) {
                AccessoryService.updateStock(item.getId(), newQuantity);
            } else {
                productService.updateStock(item.getId(), newQuantity);
            }
        }
        
        // 3. Generate Sale ID and persist
        int saleId = saleRepository.findAll().size() + 1;
        String currentDate = LocalDate.now().toString();

        Sale newSale = new Sale(saleId, currentDate, client, seller, items);
        newSale.calculateTotal();

        saleRepository.save(newSale);

        return newSale;
    }

    /**
     * Retrieves the complete history of registered sales.
     *
     * @return A list containing all sales.
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Retrieves the purchase history of a specific client.
     *
     * @param clientIdentification The identification of the client.
     * @return A list of sales made by the specified client.
     */
    public List<Sale> getSalesByClient(String clientIdentification) {
        List<Sale> allSales = saleRepository.findAll();
        List<Sale> clientSales = new ArrayList<>();

        for (Sale sale : allSales) {
            if (sale.getClient().getIdentification().equals(clientIdentification)) {
                clientSales.add(sale);
            }
        }
        return clientSales;
    }

    /**
     * Retrieves the sales history attended by a specific seller.
     *
     * @param employeeCode The employee code of the seller.
     * @return A list of sales attended by the specified seller.
     */
    public List<Sale> getSalesBySeller(String employeeCode) {
        List<Sale> allSales = saleRepository.findAll();
        List<Sale> sellerSales = new ArrayList<>();

        for (Sale sale : allSales) {
            if (sale.getSeller().getEmployeeCode().equals(employeeCode)) {
                sellerSales.add(sale);
            }
        }
        return sellerSales;
    }
}