package com.gamezone.Main;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.FileAccessoryRepository;
import com.gamezone.persistence.FilePersonRepository;
import com.gamezone.persistence.FileProductRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.FilePromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.FileReturnRepository;
import com.gamezone.persistence.ISaleRepository;
import com.gamezone.persistence.FileSaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.persistence.FileWarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.IStockService;
import com.gamezone.service.ItemStockService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.GameZoneUI;

/**
 * Main application entry point.
 * Acts as the composition root (Bootstrap/IoC Container), instantiating repositories,
 * wiring service dependencies via interfaces, and launching the interactive UI.
 */
public class Main {

    /**
     * Main method that executes application initialization and launches the UI.
     *
     * @param args command-line arguments passed during execution
     */
    public static void main(String[] args) {
        // Step 1: Initialize base persistence repositories using abstractions
        ProductRepository productRepository = new FileProductRepository();
        PersonRepository personRepository = new FilePersonRepository("persons.txt");
        AccessoryRepository accessoryRepository = new FileAccessoryRepository();
        PromotionRepository promotionRepository = new FilePromotionRepository("data/promotions.csv");

        // Step 2: Initialize base domain services
        ProductService productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);

        // Step 3: Initialize repositories for complex domain objects using ISaleRepository interface
        ISaleRepository saleRepository = new FileSaleRepository("data/sales.csv");
        WarrantyRepository warrantyRepository = new FileWarrantyRepository();

        // Step 4: Initialize WarrantyService
        WarrantyService warrantyService = new WarrantyService(warrantyRepository, saleRepository);

        // Step 5: Initialize SaleService with full dependency injection (DIP compliant)
        SaleService saleService = new SaleService(
                saleRepository,
                productService,     // Implements IStockService contract
                promotionService,
                warrantyService,
                personService,      // Required for sale reconstruction
                productService      // Required for sale reconstruction
        );

        // Step 6: Resolve circular dependency via setter injection and sync historical console warranties
        warrantyService.setSaleService(saleService);
        warrantyService.syncPastConsoleWarranties();

        // Step 7: Initialize ReturnRepository and ReturnService
        ReturnRepository returnRepository = new FileReturnRepository();
        IStockService itemStockService = new ItemStockService(productService, accessoryService);
        ReturnService returnService = new ReturnService(
                returnRepository,
                saleService,
                productService,
                accessoryService,
                warrantyService,
                itemStockService
        );

        // Step 8: Inject AccessoryService into ProductService for unified search capability
        productService.setAccessoryService(accessoryService);

        // Step 9: Wire all services into the GameZone UI controller and launch application
        GameZoneUI ui = new GameZoneUI(
                saleService,
                productService,
                personService,
                accessoryService,
                promotionService,
                returnService,
                warrantyService
        );

        ui.start();
    }
}