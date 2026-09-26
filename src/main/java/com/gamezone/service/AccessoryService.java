package com.gamezone.service;

import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;

/**
 * Provides the business rules for managing accessories (controllers, cables and
 * memories). Acts as the intermediary between the user interface and the
 * accessory persistence layer.
 */
public class AccessoryService {

    public void registerController(String id, String title, double price, int stock, String connectionType) {
        registerController(id, title, price, stock, connectionType, new ArrayList<>());
    }

    public void registerCable(String id, String title, double price, int stock, double length, String connectorType) {
        registerCable(id, title, price, stock, length, connectorType, new ArrayList<>());
    }

    public void registerMemory(String id, String title, double price, int stock, int capacity, String storageType) {
        registerMemory(id, title, price, stock, capacity, storageType, new ArrayList<>());
    }
    private AccessoryRepository accessoryRepository;
    private List<Accessory> accessories;

    /**
     * Creates an AccessoryService backed by the given repository. Loads any
     * previously stored accessories immediately.
     *
     * @param accessoryRepository the repository used to persist accessory data
     */
    public AccessoryService(AccessoryRepository accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
        this.accessories = accessoryRepository.loadAll();
    }

    /**
     * Registers a new controller and persists the updated list.
     *
     * @param id the accessory identifier
     * @param title the accessory title
     * @param price the accessory price
     * @param stock the initial stock
     * @param connectionType the connection type (wireless or wired)
     * @param compatibleConsoleIds the list of compatible console IDs
     */
    public void registerController(String id, String title, double price, int stock,
            String connectionType, List<String> compatibleConsoleIds) {
        Controller controller = new Controller(compatibleConsoleIds, id, title, price, stock, connectionType);
        accessories.add(controller);
        accessoryRepository.saveAll(accessories);
    }

    /**
     * Registers a new cable and persists the updated list.
     *
     * @param id the accessory identifier
     * @param title the accessory title
     * @param price the accessory price
     * @param stock the initial stock
     * @param length the cable length in meters
     * @param connectorType the connector type (HDMI, USB, optical, etc.)
     * @param compatibleConsoleIds the list of compatible console IDs
     */
    public void registerCable(String id, String title, double price, int stock,
            double length, String connectorType, List<String> compatibleConsoleIds) {
        Cable cable = new Cable(compatibleConsoleIds, id, title, price, stock, length, connectorType);
        accessories.add(cable);
        accessoryRepository.saveAll(accessories);
    }

    /**
     * Registers a new memory and persists the updated list.
     *
     * @param id the accessory identifier
     * @param title the accessory title
     * @param price the accessory price
     * @param stock the initial stock
     * @param capacity the storage capacity in gigabytes
     * @param storageType the storage type (SD, microSD, internal card, etc.)
     * @param compatibleConsoleIds the list of compatible console IDs
     */
    public void registerMemory(String id, String title, double price, int stock,
            int capacity, String storageType, List<String> compatibleConsoleIds) {
        Memory memory = new Memory(capacity, storageType, id, title, price, stock, compatibleConsoleIds);
        accessories.add(memory);
        accessoryRepository.saveAll(accessories);
    }

    /**
     * Returns all registered accessories, regardless of type.
     *
     * @return the full list of accessories
     */
    public List<Accessory> listAllAccessories() {
        return accessories;
    }

    /**
     * Returns all accessories of a given type.
     *
     * @param type the type to filter by: "CONTROLLER", "CABLE" or "MEMORY"
     * (case-insensitive)
     * @return the list of accessories matching the given type
     */
    public List<Accessory> listAccessoriesByType(String type) {
        List<Accessory> result = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (type.equalsIgnoreCase("CONTROLLER") && accessory instanceof Controller) {
                result.add(accessory);
            } else if (type.equalsIgnoreCase("CABLE") && accessory instanceof Cable) {
                result.add(accessory);
            } else if (type.equalsIgnoreCase("MEMORY") && accessory instanceof Memory) {
                result.add(accessory);
            }
        }
        return result;
    }

    /**
     * Returns all accessories compatible with the given console.
     *
     * @param consoleId the identifier of the console to check compatibility
     * against
     * @return the list of accessories compatible with the given console
     */
    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
        List<Accessory> result = new ArrayList<>();

        if (accessories == null || consoleId == null) {
            return result;
        }

        for (Accessory accessory : accessories) {
            List<String> compatibleConsoles = accessory.getCompatibleConsoleIds();

            // Validamos que la lista de consolas no sea nula antes de buscar
            if (compatibleConsoles != null) {
                for (String id : compatibleConsoles) {
                    // Hacemos la comparación ignorando mayúsculas/minúsculas por seguridad
                    if (id.equalsIgnoreCase(consoleId)) {
                        result.add(accessory);
                        break; // Salimos del bucle interno si ya hizo match
                    }
                }
            }
        }
        return result;
    }

    /**
     * Finds an accessory by its identifier.
     *
     * @param id the identifier to search for
     * @return the matching accessory, or null if not found
     */
    public Accessory findById(String id) {
        for (Accessory accessory : accessories) {
            if (accessory.getId().equals(id)) {
                return accessory;
            }
        }
        return null;
    }

    /**
     * Reduces the stock of an accessory by the given quantity (e.g. after a
     * sale) and persists the change.
     *
     * @param accessoryId the identifier of the accessory to update
     * @param quantity the quantity to subtract from the current stock
     * @return true if the update succeeded, false if the accessory was not
     * found or if there is not enough stock
     */
    public boolean updateStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        if (accessory == null) {
            return false;
        }
        if (accessory.getStock() < quantity) {
            return false;
        }
        accessory.setStock(accessory.getStock() - quantity);
        accessoryRepository.saveAll(accessories);
        return true;
    }
}
