package com.gamezone.service;

import com.gamezone.model.Product;
import java.util.List;

/**
 * Interface contract for product catalog management operations.
 * Segregates product lookup and registration responsibilities.
 */
public interface IProductService extends IStockService {

    List<String> getAvailableCategories();

    boolean registerVideoGame(String platform, String genre, String ageRating,
                              String id, String title, double price, int stock);

    List<Product> getAllProducts();

    Product findProductById(String id);
}