package com.gamezone.persistence;

import com.gamezone.model.Sale;
import java.util.List;
import java.util.Optional;

public interface ISaleRepository {
    Sale save(Sale sale);
    Optional<Sale> findById(String id);
    List<Sale> findAll();
    boolean delete(String id);
}