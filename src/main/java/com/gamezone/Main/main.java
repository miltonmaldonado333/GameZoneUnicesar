package com.gamezone.Main;

import com.gamezone.persistence.FilePersonRepository;
import com.gamezone.persistence.FileProductRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.SaleRepository;
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
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ProductRepository productRepo = new FileProductRepository();
        PersonRepository personRepo = new FilePersonRepository("persons.txt");
        
        ProductService productService = new ProductService(productRepo);
        PersonService personService = new PersonService(personRepo);
        
        SaleRepository saleRepo = new SaleRepository(personService, productService);
        SaleService saleService = new SaleService(saleRepo, productService);

        GameZoneUI ui = new GameZoneUI(saleService, productService, personService);
        ui.start();
    }
}