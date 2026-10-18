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
 * Service class handling product business logic.
 * Input and existence checks are delegated to ProductValidator.
 */
public class ProductService {

    private final ProductRepository productRepository;
    private final List<Product> products;

    /**
     * Receives the repository and loads the saved products into memory.
     *
     * @param productRepository the repository used to persist product data
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = productRepository.loadProducts();
    }

    /**
     * Finds a product by its unique ID.
     *
     * @param id the identifier to search for
     * @return the matching product, or null if not found
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
     * Registers a new video game if the ID is unique and saves to file.
     *
     * @param platform the platform the game runs on
     * @param genre    the game genre
     * @param ageRating the recommended age rating
     * @param id       the product identifier
     * @param title    the product title
     * @param price    the product price
     * @param stock    the initial stock
     * @return true if registered, false if the ID already exists
     * @throws InvalidDataException if the id, title, price or stock are not valid
     */
    public boolean registerVideoGame(String platform, String genre, String ageRating, String id, String title, double price, int stock) {
        ProductValidator.validateProductData(id, title, price, stock);
        if (findProductById(id) != null) {
            return false; // ID ALREADY EXIST
        }
        VideoGame game = new VideoGame(platform, genre, ageRating, id, title, price, stock);
        products.add(game);
        productRepository.saveProducts(products);
        return true;
    }

    /**
     * Registers a new console if the ID is unique and saves to file.
     *
     * @param brand      the console brand
     * @param model      the console model
     * @param generation the console generation
     * @param id         the product identifier
     * @param title      the product title
     * @param price      the product price
     * @param stock      the initial stock
     * @return true if registered, false if the ID already exists
     * @throws InvalidDataException if the id, title, price or stock are not valid
     */
    public boolean registerConsole(String brand, String model, String generation, String id, String title, double price, int stock) {
        ProductValidator.validateProductData(id, title, price, stock);
        if (findProductById(id) != null) {
            return false; // ID ALREADY EXIST
        }
        Console console = new Console(brand, model, generation, id, title, price, stock);
        products.add(console);
        productRepository.saveProducts(products);
        return true;
    }

    /**
     * Returns a copy of the complete product list.
     *
     * @return a new list with every registered product
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Deducts the sold quantity from the product stock and saves to file.
     *
     * @param productId    the identifier of the product sold
     * @param quantitySold the quantity to subtract from the stock
     */
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

    /**
     * Adds the given quantity back to the stock of an existing product.
     *
     * @param productId the identifier of the product to restore
     * @param quantity  the quantity to add to the stock
     * @throws ResourceNotFoundException if the product does not exist
     */
    public void restoreStock(String productId, int quantity) {
        Product product = findProductById(productId);
        ProductValidator.validateProductExists(product, productId);
        product.setStock(product.getStock() + quantity);
        productRepository.saveProducts(products);
    }
}