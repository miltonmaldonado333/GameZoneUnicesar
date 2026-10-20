package com.gamezone.persistence;

import java.util.List;

import com.gamezone.model.Warranty;

/**
 * Defines the persistence operations available for warranties. Stored warranties
 * are loaded as raw WarrantyRecord objects and the service layer rebuilds the
 * Warranty objects from them. Implementations choose how and where the data is stored.
 */
public interface WarrantyRepository {

    /**
     * Loads the raw records of all stored warranties.
     *
     * @return the list of raw warranty records; empty if nothing has been stored yet
     */
    List<WarrantyRecord> loadAll();

    /**
     * Saves the given warranties, replacing any previously stored data.
     *
     * @param warranties the list of warranties to save
     */
    void saveAll(List<Warranty> warranties);
}