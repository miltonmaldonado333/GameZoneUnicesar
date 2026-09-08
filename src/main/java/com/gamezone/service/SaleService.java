package com.gamezone.service;

import com.gamezone.model.Sale;
import com.gamezone.model.Product;
import com.gamezone.persistence.SaleRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing the business logic of the sales module.
 * It handles sale registrations, inventory updates, and historical queries.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;

    /**
     * Constructs a SaleService with the required dependencies.
     *
     * @param saleRepository The repository used to persist sales.
     * @param productService The service used to manage product inventory.
     */
    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    /**
     * Registers a new sale after applying business validations.
     * It ensures the sale has at least one product, checks stock availability,
     * and updates the inventory automatically.
     *
     * @param sale The sale object to be registered.
     * @throws IllegalArgumentException if the validation fails.
     */
    public void registerSale(Sale sale) {
        List<Product> purchasedProducts = sale.getProducts();

        if (purchasedProducts == null || purchasedProducts.isEmpty()) {
            throw new IllegalArgumentException("Error: A sale must contain at least one product.");
        }

        for (Product product : purchasedProducts) {
            if (product.getStock() <= 0) {
                throw new IllegalArgumentException("Error: Insufficient stock for product ID " + product.getId());
            }
        }

        for (Product product : purchasedProducts) {
            productService.decreaseStock(product.getId(), 1); 
        }

        saleRepository.save(sale);
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
     * @param clientId The ID of the client.
     * @return A list of sales made by the specified client.
     */
    public List<Sale> getSalesByClient(String clientId) {
        List<Sale> allSales = saleRepository.findAll();
        List<Sale> clientSales = new ArrayList<>();
        
        for (Sale sale : allSales) {
            if (sale.getClient().getId().equals(clientId)) {
                clientSales.add(sale);
            }
        }
        return clientSales;
    }

    /**
     * Retrieves the sales history attended by a specific seller.
     *
     * @param sellerId The ID of the seller.
     * @return A list of sales attended by the specified seller.
     */
    public List<Sale> getSalesBySeller(String sellerId) {
        List<Sale> allSales = saleRepository.findAll();
        List<Sale> sellerSales = new ArrayList<>();
        
        for (Sale sale : allSales) {
            if (sale.getSeller().getId().equals(sellerId)) {
                sellerSales.add(sale);
            }
        }
        return sellerSales;
    }
}