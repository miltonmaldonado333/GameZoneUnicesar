package com.gamezone.persistence;

import java.util.List;

import com.gamezone.model.Return;

/**
 * Defines the persistence operations available for returns. Stored returns are
 * loaded as raw ReturnRecord objects and the service layer rebuilds the Return
 * objects from them. Implementations choose how and where the data is stored.
 */
public interface ReturnRepository {

    /**
     * Loads the raw records of all stored returns.
     *
     * @return the list of raw return records; empty if nothing has been stored yet
     */
    List<ReturnRecord> loadAll();

    /**
     * Saves the given returns, replacing any previously stored data.
     *
     * @param returns the list of returns to save
     */
    void saveAll(List<Return> returns);
}