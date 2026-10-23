package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service handling product catalog operations, stock updates, and entity registrations.
 * Implements unified product service contracts following SOLID principles.
 */
public class ProductService implements IProductService, IStockService {

    private final ProductRepository productRepository;
    private AccessoryService accessoryService;

    /**
     * Constructs a ProductService instance with the required repository.
     *
     * @param productRepository the persistence repository for managing product data
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
            throw new BusinessRuleException("Producto no encontrado con ID: " + productId);
        }

        targetProduct.setStock(targetProduct.getStock() + quantity);
        productRepository.saveProducts(products);
    }

    @Override
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

    @Override
    public boolean registerVideoGame(String platform, String genre, String ageRating,
                                     String id, String title, double price, int stock) {
        List<Product> products = productRepository.loadProducts();
        if (products == null) {
            products = new ArrayList<>();
        }

        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return false;
            }
        }

        VideoGame game = new VideoGame(platform, genre, ageRating, id, title, price, stock);
        products.add(game);

        productRepository.saveProducts(products);
        return true;
    }

    /**
     * Registers a new Gaming Console entity in the product catalog.
     *
     * @param brand      manufacturer brand
     * @param model      console model name
     * @param generation console hardware generation
     * @param id         unique product identifier
     * @param title      display title
     * @param price      unit retail price
     * @param stock      available inventory stock
     * @return true if successfully saved, false if product ID already exists
     */
    @Override
    public boolean registerConsole(String brand, String model, String generation,
                                   String id, String title, double price, int stock) {
        List<Product> products = productRepository.loadProducts();
        if (products == null) {
            products = new ArrayList<>();
        }

        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return false;
            }
        }

        Console console = new Console(brand, model, generation, id, title, price, stock);
        products.add(console);

        productRepository.saveProducts(products);
        return true;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findProductById(String id) {
        return productRepository.findById(id);
    }
}