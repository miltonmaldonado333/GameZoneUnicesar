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
 * Main application entry point.
 * Acts as the composition root (Bootstrap), instantiating repositories,
 * wiring service dependencies, and launching the interactive UI.
 */
public class main {

    public static void main(String[] args) {
        // 1. Base Repositories
        ProductRepository productRepository = new FileProductRepository();
        PersonRepository personRepository = new FilePersonRepository("persons.txt");
        AccessoryRepository accessoryRepository = new FileAccessoryRepository();
        PromotionRepository promotionRepository = new FilePromotionRepository();

        // 2. Base Services
        ProductService productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository, productRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);

        // 3. Repositories depending on base Services
        SaleRepository saleRepository = new SaleRepository(personService, productService);
        WarrantyRepository warrantyRepository = new WarrantyRepository();

        // 4. Warranty Service (Full Dependency Injection)
        WarrantyService warrantyService = new WarrantyService(warrantyRepository, saleRepository);

        // 5. Sale Service
        SaleService saleService = new SaleService(saleRepository, productService, accessoryService, promotionService, warrantyService);

        // 6. Return Repository and Service
        ReturnRepository returnRepository = new ReturnRepository();
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService, accessoryService, warrantyService);

        // 7. Dependency Injection for unified search
        productService.setAccessoryService(accessoryService);

        // 8. Launch User Interface
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