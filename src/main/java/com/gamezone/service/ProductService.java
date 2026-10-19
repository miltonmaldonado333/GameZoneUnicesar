package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.validation.ProductValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling product domain logic, stock management, and unified catalog queries.
 * 
 * Applies GRASP principles by acting as a Controller for product creation and 
 * delegating state mutation directly to domain entities (Information Expert).
 */
public class ProductService {

    private final ProductRepository productRepository;
    private final List<Product> products;
    private AccessoryService accessoryService;

    /**
     * Constructs a {@code ProductService} using the specified repository to load initial data.
     *
     * @param productRepository the repository responsible for product persistence
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = productRepository.loadProducts();
    }

    /**
     * Optional dependency setter for {@link AccessoryService} to enable unified 
     * item searches across both products and accessories.
     *
     * @param accessoryService the accessory service instance
     */
    public void setAccessoryService(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param id the identifier to search for
     * @return the matching {@link Product}, or {@code null} if not found
     */
    public Product findProductById(String id) {
        for (Product product : products) {
            if (product.getId().equalsIgnoreCase(id)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Unifies item lookup across products and accessories for sales operations.
     * 
     * GRASP Indirection & Controller: Centralizes fallback searching so UI 
     * components do not catch sequential exceptions between separate services.
     *
     * @param id the identifier of the product or accessory
     * @return the saleable {@link Product} item found, or {@code null} if it does not exist
     */
    public Product findSaleableItemById(String id) {
        Product item = findProductById(id);
        if (item == null && accessoryService != null) {
            try {
                item = accessoryService.findById(id);
            } catch (ResourceNotFoundException ignored) {
                // Fallback returns null if not found in accessory repository
            }
        }
        return item;
    }

    /**
     * Dynamically extracts all distinct product categories currently available in the system.
     * 
     * GRASP High Cohesion: Encapsulates category discovery in the service layer 
     * to eliminate reflection logic in UI controllers.
     *
     * @return a list of unique category names
     */
    public List<String> getAvailableCategories() {
        List<String> categories = new ArrayList<>();
        for (Product p : products) {
            String category = (p.getCategory() != null && !p.getCategory().isBlank()) 
                    ? p.getCategory() 
                    : p.getClass().getSimpleName().toUpperCase();
            
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }

        if (accessoryService != null && accessoryService.listAllAccessories() != null 
                && !accessoryService.listAllAccessories().isEmpty()) {
            if (!categories.contains("ACCESSORY")) {
                categories.add("ACCESSORY");
            }
        }
        return categories;
    }

    /**
     * Registers a new video game if the ID is unique and persists changes.
     *
     * @param platform  the target platform
     * @param genre     the game genre
     * @param ageRating the target age classification
     * @param id        the unique product identifier
     * @param title     the product title
     * @param price     the retail price
     * @param stock     the initial stock level
     * @return {@code true} if registered successfully, {@code false} if the ID already exists
     * @throws InvalidDataException if validation fails for basic product fields
     */
    public boolean registerVideoGame(String platform, String genre, String ageRating, String id, String title, double price, int stock) {
        ProductValidator.validateProductData(id, title, price, stock);
        
        // GRASP Pure Fabrication / High Cohesion (V-11): Delegate existence check to ProductValidator
        if (findProductById(id) != null) {
            ProductValidator.validateUniqueProduct(findProductById(id), id);
            return false;
        }
        
        VideoGame game = new VideoGame(platform, genre, ageRating, id, title, price, stock);
        products.add(game);
        productRepository.saveProducts(products);
        return true;
    }

    /**
     * Registers a new gaming console if the ID is unique and persists changes.
     *
     * @param brand      the console manufacturer
     * @param model      the console model
     * @param generation the console generation
     * @param id         the unique product identifier
     * @param title      the product title
     * @param price      the retail price
     * @param stock      the initial stock level
     * @return {@code true} if registered successfully, {@code false} if the ID already exists
     * @throws InvalidDataException if validation fails for basic product fields
     */
    public boolean registerConsole(String brand, String model, String generation, String id, String title, double price, int stock) {
        ProductValidator.validateProductData(id, title, price, stock);
        
        // GRASP Pure Fabrication / High Cohesion (V-11): Delegate existence check to ProductValidator
        if (findProductById(id) != null) {
            ProductValidator.validateUniqueProduct(findProductById(id), id);
            return false;
        }
        
        Console console = new Console(brand, model, generation, id, title, price, stock);
        products.add(console);
        productRepository.saveProducts(products);
        return true;
    }

    /**
     * Returns a defensive copy of all registered products in the inventory.
     *
     * @return a new list containing all current products
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Subtracts the sold quantity from product inventory and saves the state.
     * 
     * GRASP Information Expert (V-10): Delegates stock reduction directly to 
     * product.reduceStock(quantitySold) instead of performing arithmetic externally.
     *
     * @param productId    the unique identifier of the sold product
     * @param quantitySold the quantity sold to subtract
     */
    public void updateStock(String productId, int quantitySold) {
        Product product = findProductById(productId);
        if (product != null) {
            // Delegate state mutation to Product (Information Expert - V-10)
            product.reduceStock(quantitySold);
            productRepository.saveProducts(products);
        }
    }

    /**
     * Restores stock for a product when a sale is returned or cancelled.
     * 
     * GRASP Information Expert: Delegates state mutation to the domain entity method 
     * (e.g., {@code product.addStock(quantity)}) instead of directly calculating stock in the service.
     *
     * @param productId the identifier of the product to restore
     * @param quantity  the quantity to add back to inventory
     * @throws ResourceNotFoundException if the product does not exist
     */
    public void restoreStock(String productId, int quantity) {
        Product product = findProductById(productId);
        ProductValidator.validateProductExists(product, productId);
        
        // Delegate state update to Product (Information Expert)
        product.addStock(quantity);
        productRepository.saveProducts(products);
    }
}