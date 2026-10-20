package com.gamezone.persistence;

import java.util.List;

import com.gamezone.model.Accessory;

/**
 * Defines the persistence operations available for accessories (controllers,
 * cables and memories). Implementations choose how and where the data is stored.
 */
public interface AccessoryRepository {

    /**
     * Loads all stored accessories.
     *
     * @return the list of stored accessories; empty if nothing has been stored yet
     */
    List<Accessory> loadAll();

    /**
     * Saves the given accessories, replacing any previously stored data.
     *
     * @param accessories the list of accessories to save
     */
    void saveAll(List<Accessory> accessories);
}