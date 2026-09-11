package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class handling product business logic.
 */
public class ProductService {

    private final ProductRepository productRepository;
    private final List<Product> products;
   // Constructor: Receives the repository and loads saved products into memory
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = productRepository.loadProducts();
    }

    /**
     * Finds a product by its unique ID.
     */
    public Product findProductById(String id) {
        for (Product product : products) {
            if (product.getId().equalsIgnoreCase(id)) {
                return product;
            }
        }
        return null;
    }
    // Registers a new video game if the ID is unique and saves to file
    public boolean registerVideoGame(String platform, String genre, String ageRating, String id, String title, double price, int stock) {
        if (findProductById(id) != null) {
            return false; // ID ALREADY EXIST
        }
        VideoGame game = new VideoGame(platform, genre, ageRating, id, title, price, stock);
        products.add(game);
        productRepository.saveProducts(products);
        return true;
    }
    // Registers a new console if the ID is unique and saves to file
    public boolean registerConsole(String brand, String model, String generation, String id, String title, double price, int stock) {
        if (findProductById(id) != null) {
            return false; //ID ALREADY EXIST
        }
        Console console = new Console(brand, model, generation, id, title, price, stock);
        products.add(console);
        productRepository.saveProducts(products);
        return true;
    }
    // Returns a copy of the complete product list
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Deducts sold quantity from product stock and saves to file
    public void updateStock(String productId, int quantitySold) {
        Product product = findProductById(productId);
        if (product != null) {
            int newStock = product.getStock() - quantitySold;
            if (newStock >= 0) {
                product.setStock(newStock);
                productRepository.saveProducts(products);
            }
        }
    }
}
