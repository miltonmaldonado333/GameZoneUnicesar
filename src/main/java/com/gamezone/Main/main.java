package com.gamezone.Main;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.FilePersonRepository;
import com.gamezone.persistence.FileProductRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.GameZoneUI;

/**
 * Punto de entrada principal para el sistema GameZone Unicesar.
 * Inicializa la arquitectura por capas e inicia la interfaz de consola.
 */
public class main {

    /**
     * Método principal que arranca la aplicación.
     * Inicializa repositorios, servicios y la interfaz de usuario.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {

        // 1. Inicializar la capa de persistencia (Repositorios)
        
        ProductRepository productRepo = new FileProductRepository();
        PersonRepository personRepo = new FilePersonRepository("persons.txt"); 
        AccessoryRepository accessoryRepo = new AccessoryRepository();
        PromotionRepository promotionRepo = new PromotionRepository();

        // 2. Inicializar la capa de negocio (Servicios)
        ProductService productService = new ProductService(productRepo);
        PersonService personService = new PersonService(personRepo);
        AccessoryService accessoryService = new AccessoryService(accessoryRepo);
        PromotionService promotionService = new PromotionService(promotionRepo);

        // 3. Inicializar el módulo de ventas con sus dependencias
        SaleRepository saleRepo = new SaleRepository(personService, productService);
        SaleService saleService = new SaleService(saleRepo, productService, accessoryService, promotionService);

        // 4. Inicializar y arrancar la capa de presentación (UI)
        GameZoneUI ui = new GameZoneUI(saleService, productService, personService, accessoryService, promotionService);
        ui.start();
    }
}