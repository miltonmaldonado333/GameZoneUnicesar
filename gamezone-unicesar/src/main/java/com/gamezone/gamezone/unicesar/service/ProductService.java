package com.gamezone.gamezone.unicesar.service;

import com.gamezone.gamezone.unicesar.model.Console;
import com.gamezone.gamezone.unicesar.model.Product;
import com.gamezone.gamezone.unicesar.model.VideoGame;
import com.gamezone.gamezone.unicesar.persistence.ProductRepository;
import java.util.ArrayList;
import java.util.List;


/**
 * Service class handling product business logic.
 */
public class ProductService {
    private final ProductRepository productRepository;
    private final List<Product> products;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = productRepository.loadProducts();
    }

    public void registerVideoGame(String platform, String genre, String ageRating, String id, String title, double price, int stock) {
        VideoGame game = new VideoGame(platform,genre,ageRating,id,title,price,stock);
        products.add(game);
        productRepository.saveProducts(products);
    }

    public void registerConsole(String brand, String model, String generation, String id, String title, double price, int stock) {
        Console console = new Console(brand,model,generation,id,title,price,stock);
        products.add(console);
        productRepository.saveProducts(products);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product findProductById(String id) {
        for (Product product : products) {
            if (product.getId().equalsIgnoreCase(id)) {
                return product;
            }
        }
        return null;
    }

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