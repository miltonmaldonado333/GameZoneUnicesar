
package com.gamezone.persistence;

import com.gamezone.model.Product;
import java.util.List;

/**
 * Interface defining persistence operations for products
 * @author Jesus
 */

 public interface ProductRepository {
    List<Product>loadProducts();
    void saveProducts(List<Product> products);
}

