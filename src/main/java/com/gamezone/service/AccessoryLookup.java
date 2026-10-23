package com.gamezone.service;

import com.gamezone.model.Accessory;

/**
 * Narrow abstraction for finding an accessory by its identifier, so the classes
 * that rebuild stored data do not depend on the whole accessory service.
 */
public interface AccessoryLookup {

    /**
     * Finds an accessory by its identifier.
     *
     * @param id the identifier to search for
     * @return the matching accessory, or null if not found
     */
    Accessory findById(String id);
}