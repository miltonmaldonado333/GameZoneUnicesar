package com.gamezone.persistence;

import java.util.List;

import com.gamezone.model.Promotion;

/**
 * Defines the persistence operations available for promotions. Implementations
 * choose how and where the data is stored.
 */
public interface PromotionRepository {

    /**
     * Loads all stored promotions.
     *
     * @return the list of stored promotions; empty if nothing has been stored yet
     */
    List<Promotion> loadAll();

    /**
     * Saves the given promotions, replacing any previously stored data.
     *
     * @param promotions the list of promotions to save
     */
    void saveAll(List<Promotion> promotions);
}