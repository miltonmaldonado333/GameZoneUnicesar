package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.model.Product;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.model.VideoGame;
import java.util.ArrayList;

import java.util.List;

/**
 * Service handling product catalog operations and implementing the unified
 * stock management contract.
 */
public class ProductService implements IStockService {

    private final ProductRepository productRepository;
    private AccessoryService accessoryService;

    /**
     * Constructs a ProductService instance with the required repository.
     *
     * @param productRepository the persistence repository for managing product
     * data
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    public void setAccessoryService(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    @Override
    public boolean hasSufficientStock(String productId, int requestedQuantity) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            return false;
        }
        return product.getStock() >= requestedQuantity;
    }

    @Override
    public void reduceStock(String productId, int quantity) {
        List<Product> products = productRepository.loadProducts();

        //Here it's compare ids for found a product id
        Product targetProduct = null;
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(productId)) {
                targetProduct = p;
                break;
            }
        }

        if (targetProduct == null) {
            throw new BusinessRuleException("Producto no encontrado con ID: " + productId);
        }
        if (targetProduct.getStock() < quantity) {
            throw new BusinessRuleException("Stock no suficiente para el producto con el ID: " + productId);
        }

        targetProduct.setStock(targetProduct.getStock() - quantity);

        productRepository.saveProducts(products);
    }

    @Override
    public void restoreStock(String productId, int quantity) {
        List<Product> products = productRepository.loadProducts();

        Product targetProduct = null;
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(productId)) {
                targetProduct = p;
                break;
            }
        }

        if (targetProduct == null) {
            throw new BusinessRuleException("Producto no encontado con ID: " + productId);
        }

        targetProduct.setStock(targetProduct.getStock() + quantity);

        productRepository.saveProducts(products);
    }

    public List<String> getAvailableCategories() {
        List<Product> products = productRepository.loadProducts();

        List<String> categories = new ArrayList<>();
        if (products != null) {
            for (Product p : products) {
                String category = p.getCategory();
                if (category != null && !categories.contains(category.toUpperCase())) {
                    categories.add(category.toUpperCase());
                }
            }
        }
        return categories;
    }

    // Registers a new VideoGame product in the inventory after checking for ID uniqueness.
    // @param platform target gaming console or system platform
    // @param genre game genre or category
    // @param ageRating age classification rating
    // @param id unique product identifier
    // @param title display name of the video game
    // @param price unit sale price
    // @param stock initial quantity available in inventory
    // @return true if successfully registered; false if a product with the same ID already exists
    public boolean registerVideoGame(String platform, String genre, String ageRating,
            String id, String title, double price, int stock) {
        List<Product> products = productRepository.loadProducts();
        if (products == null) {
            products = new ArrayList<>();
        }

        // Check for duplicate ID
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return false;
            }
        }

        // Create new VideoGame entity and add to catalog
        VideoGame game = new VideoGame(platform, genre, ageRating, id, title, price, stock);
        products.add(game);

        // Save updated catalog to repository
        productRepository.saveProducts(products);
        return true;
    }

    /**
     * Retrieves all products registered in the catalog.
     *
     * @return a list containing all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param id the unique identifier of the product
     * @return the matching product instance, or null if not found
     */
    public Product findProductById(String id) {
        return productRepository.findById(id);
    }

}
