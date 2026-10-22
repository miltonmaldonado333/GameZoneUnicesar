package com.gamezone.service;

import com.gamezone.model.Product;
import java.util.List;

public interface IProductService {

    boolean registerVideoGame(String platform, String genre, String ageRating,
                              String id, String title, double price, int stock);

    // AGREGAR ESTA LÍNEA:
    boolean registerConsole(String brand, String model, String generation,
                            String id, String title, double price, int stock);

    List<Product> getAllProducts();

    Product findProductById(String id);

    List<String> getAvailableCategories();
}