package com.gamezone.persistence;

import com.gamezone.model.Product;
import java.util.List;

/**
 * Interface defining persistence operations for products.
 * @author Jesus
 */
public interface ProductRepository {
    
    // Loads the full list of products from persistence.
    List<Product> loadProducts();
    
    // Saves the full list of products to persistence.
    void saveProducts(List<Product> products);

    // Default method to find a product by its unique identifier.
    default Product findById(String id) {
        if (id == null) {
            return null;
        }
        return loadProducts().stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
    
    default List<Product> findAll() {
        return loadProducts();
    }
}