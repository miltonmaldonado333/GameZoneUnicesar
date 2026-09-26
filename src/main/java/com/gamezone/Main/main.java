package com.gamezone.Main;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.FilePersonRepository;
import com.gamezone.persistence.FileProductRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.GameZoneUI;

/**
 * Main entry point for the GameZone Unicesar system.
 * Initializes the layered architecture components and starts the interactive console interface.
 */
public class main {

    /**
     * Application entry point. Initializes persistence repositories, 
     * business services, and user interface before launching the application loop.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // 1. Initialize Persistence Layer (Repositories)
        ProductRepository productRepo = new FileProductRepository();
        PersonRepository personRepo = new FilePersonRepository("persons.txt"); 
        AccessoryRepository accessoryRepo = new AccessoryRepository();
        PromotionRepository promotionRepo = new PromotionRepository();

        // 2. Initialize Core Services
        ProductService productService = new ProductService(productRepo);
        PersonService personService = new PersonService(personRepo);
        AccessoryService accessoryService = new AccessoryService(accessoryRepo);
        PromotionService promotionService = new PromotionService(promotionRepo);

        // 3. Initialize Sales Base Layer
        SaleRepository saleRepo = new SaleRepository(personService, productService);
        
        // Crear la instancia inicial de SaleService con 4 parámetros
        SaleService saleService = new SaleService(saleRepo, productService, accessoryService, promotionService);

        // 4. Initialize Warranty Module (pasando saleService en minúsculas)
        WarrantyRepository warrantyRepository = new WarrantyRepository(saleService, productService);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository);

        saleService = new SaleService(saleRepo, productService, accessoryService, promotionService, warrantyService);
       
        // 6. Initialize Returns Module
        ReturnRepository returnRepository = new ReturnRepository(saleService, productService);
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService);
    
        warrantyService.syncPastConsoleWarranties(saleService);

        // 7. Launch UI with all fully configured services
        GameZoneUI ui = new GameZoneUI(saleService, productService, personService, accessoryService, promotionService, returnService, warrantyService);
        ui.start();
    }
}