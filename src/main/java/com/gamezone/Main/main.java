package com.gamezone.Main;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.FilePersonRepository;
import com.gamezone.persistence.FileProductRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.GameZoneUI;

/**
 * Main application entry point for the GameZone Unicesar system.
 * Initializes the layered architecture and starts the console interface.
 */
public class main {
    
    /**
     * Main method that boots up the application.
     * Initializes repositories, services, and the user interface.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        
        // Initialize the persistence layer (Repositories)
        ProductRepository productRepo = new FileProductRepository();
        PersonRepository personRepo = new FilePersonRepository("persons.txt"); 
        AccessoryRepository accessoryRepo = new AccessoryRepository();
        
        // Initialize the service layer (Business Logic)
        ProductService productService = new ProductService(productRepo);
        PersonService personService = new PersonService(personRepo);
        AccessoryService accessoryService = new AccessoryService(accessoryRepo);
        
        // Initialize the sales module with required dependencies
        SaleRepository saleRepo = new SaleRepository(personService, productService);
        SaleService saleService = new SaleService(saleRepo, productService, accessoryService);

        // Initialize and start the presentation layer (UI)
        GameZoneUI ui = new GameZoneUI(saleService, productService, personService, accessoryService);
        ui.start();
    }
}