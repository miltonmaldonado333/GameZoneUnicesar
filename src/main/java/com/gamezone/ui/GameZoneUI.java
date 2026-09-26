package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles the interactive console user interface for the GameZone system.
 * It provides menus to execute all required operations, grouping them into Product,
 * Person, Sales, Accessory, Promotion, Return, and Warranty management.
 */
public class GameZoneUI {

    // Service and input dependencies
    private final BufferedReader reader;
    private final SaleService saleService;
    private final ProductService productService;
    private final PersonService personService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final ReturnService returnService;
    private final WarrantyService warrantyService;

    /**
     * Constructs a GameZoneUI with all required service dependencies.
     *
     * @param saleService      the service managing sales
     * @param productService   the service managing products
     * @param personService    the service managing persons (clients and sellers)
     * @param accessoryService the service managing accessories
     * @param promotionService the service managing promotions
     * @param returnService    the service managing returns
     * @param warrantyService  the service managing warranties
     */
    public GameZoneUI(SaleService saleService, ProductService productService, 
                      PersonService personService, AccessoryService accessoryService,
                      PromotionService promotionService, ReturnService returnService,
                      WarrantyService warrantyService) {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.saleService = saleService;
        this.productService = productService;
        this.personService = personService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.returnService = returnService;
        this.warrantyService = warrantyService;
    }

    /**
     * Starts the main interactive application loop.
     */
    public void start() {
        int option = -1;

        System.out.println("===========================================");
        System.out.println("         WELCOME TO GAMEZONE SYSTEM        ");
        System.out.println("===========================================");

        // Main menu loop
        do {
            displayMainMenu();
            option = readOption();
            processMainMenuOption(option);
        } while (option != 0);

        System.out.println("Exiting GameZone system... Goodbye!");

        // Close the input reader safely
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing reader: " + e.getMessage());
        }
    }

    /**
     * Displays the main menu options to the console.
     */
    private void displayMainMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println("1. Product Management (Consoles and Video Games)");
        System.out.println("2. Person Management");
        System.out.println("3. Sales Management");
        System.out.println("4. Accessory Management");
        System.out.println("5. Promotion Management");
        System.out.println("6. Return Management");
        System.out.println("7. Consult Monthly Balance");
        System.out.println("8. Warranty Management");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    /**
     * Reads and parses a numeric user input from the console.
     *
     * @return the entered option or -1 if input is invalid
     */
    private int readOption() {
        try {
            String input = reader.readLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid numeric option.");
            return -1;
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Routes the main menu selection to its respective submenu handler.
     *
     * @param option the selected menu option
     */
    private void processMainMenuOption(int option) {
        switch (option) {
            case 1 -> handleProductMenu();
            case 2 -> handlePersonMenu();
            case 3 -> handleSalesMenu();
            case 4 -> handleAccessoryMenu();
            case 5 -> handlePromotionMenu();
            case 6 -> handleReturnMenu();
            case 7 -> generateMonthlyBalance();
            case 8 -> handleWarrantyMenu();
            case 0, -1 -> { }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    // ================= PRODUCT SUBMENU =================

    /**
     * Handles product management options loop.
     */
    private void handleProductMenu() {
        int option = -1;
        do {
            System.out.println("\n============= PRODUCT MANAGEMENT ==============");
            System.out.println("1. Register a new video game");
            System.out.println("2. Register a new console");
            System.out.println("3. List all available products in inventory");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            switch (option) {
                case 1 -> registerVideoGame();
                case 2 -> registerConsole();
                case 3 -> listProducts();
                case 0 -> { }
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Registers a new video game into the inventory.
     */
    private void registerVideoGame() {
        try {
            System.out.println("\n--- REGISTER VIDEO GAME ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Title: "); String title = reader.readLine();
            System.out.print("Price: "); double price = Double.parseDouble(reader.readLine());
            System.out.print("Stock: "); int stock = Integer.parseInt(reader.readLine());
            System.out.print("Platform: "); String platform = reader.readLine();
            System.out.print("Genre: "); String genre = reader.readLine();
            System.out.print("Age Rating: "); String ageRating = reader.readLine();

            boolean registered = productService.registerVideoGame(platform, genre, ageRating, id, title, price, stock);
            if (registered) {
                System.out.println("Video game registered and saved successfully!");
            } else {
                System.out.println("Error: A product with this ID already exists.");
            }
        } catch (Exception e) {
            System.out.println("Error registering video game: " + e.getMessage());
        }
    }

    /**
     * Registers a new console into the inventory.
     */
    private void registerConsole() {
        try {
            System.out.println("\n--- REGISTER CONSOLE ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Title: "); String title = reader.readLine();
            System.out.print("Price: "); double price = Double.parseDouble(reader.readLine());
            System.out.print("Stock: "); int stock = Integer.parseInt(reader.readLine());
            System.out.print("Brand: "); String brand = reader.readLine();
            System.out.print("Model: "); String model = reader.readLine();
            System.out.print("Generation: "); String generation = reader.readLine();

            boolean registered = productService.registerConsole(brand, model, generation, id, title, price, stock);
            if (registered) {
                System.out.println("Console registered and saved successfully!");
            } else {
                System.out.println("Error: A product with this ID already exists.");
            }
        } catch (Exception e) {
            System.out.println("Error registering console: " + e.getMessage());
        }
    }

    /**
     * Lists all registered products in the inventory.
     */
    private void listProducts() {
        System.out.println("\n--- PRODUCT INVENTORY ---");
        List<Product> products = productService.getAllProducts();
        if (products == null || products.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            for (Product p : products) {
                System.out.println(p.getFullDescription());
            }
        }
    }

    // ================= PERSON SUBMENU =================

    /**
     * Handles person management options loop (Clients and Sellers).
     */
    private void handlePersonMenu() {
        int option = -1;
        do {
            System.out.println("\n============= PERSON MANAGEMENT ==============");
            System.out.println("1. Register a new client");
            System.out.println("2. Register a new seller");
            System.out.println("3. List all registered clients");
            System.out.println("4. List all registered sellers");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            switch (option) {
                case 1 -> registerClient();
                case 2 -> registerSeller();
                case 3 -> listClients();
                case 4 -> listSellers();
                case 0 -> { }
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Registers a new client in the system.
     */
    private void registerClient() {
        try {
            System.out.println("\n--- REGISTER CLIENT ---");
            System.out.print("Name: "); String name = reader.readLine();
            System.out.print("Identification: "); String id = reader.readLine();
            System.out.print("Phone: "); String phone = reader.readLine();
            System.out.print("Email: "); String email = reader.readLine();

            personService.registerClient(name, id, phone, email);
            System.out.println("Client registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering client: " + e.getMessage());
        }
    }

    /**
     * Registers a new seller in the system.
     */
    private void registerSeller() {
        try {
            System.out.println("\n--- REGISTER SELLER ---");
            System.out.print("Name: "); String name = reader.readLine();
            System.out.print("Identification: "); String identification = reader.readLine();
            System.out.print("Phone: "); String phone = reader.readLine();
            System.out.print("Employee Code: "); String employeeCode = reader.readLine();
            System.out.print("Work Shift: "); String workShift = reader.readLine();

            personService.registerSeller(name, identification, phone, employeeCode, workShift);
            System.out.println("Seller registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering seller: " + e.getMessage());
        }
    }

    /**
     * Lists all registered clients.
     */
    private void listClients() {
        System.out.println("\n--- CLIENT LIST ---");
        List<Client> clients = personService.getAllClients();
        if (clients == null || clients.isEmpty()) {
            System.out.println("No clients registered.");
        } else {
            for (Client c : clients) {
                System.out.println("ID: " + c.getIdentification() + " | Name: " + c.getName() + " | Email: " + c.getEmail());
            }
        }
    }

    /**
     * Lists all registered sellers.
     */
    private void listSellers() {
        System.out.println("\n--- SELLER LIST ---");
        List<Seller> sellers = personService.getAllSellers();
        if (sellers == null || sellers.isEmpty()) {
            System.out.println("No sellers registered.");
        } else {
            for (Seller s : sellers) {
                System.out.println("Code: " + s.getEmployeeCode() + " | Name: " + s.getName() + " | Shift: " + s.getWorkShift());
            }
        }
    }

    // ================= SALES SUBMENU =================

    /**
     * Handles sales management options loop.
     */
    private void handleSalesMenu() {
        int option = -1;
        do {
            System.out.println("\n============= SALES MANAGEMENT ==============");
            System.out.println("1. Register a new sale");
            System.out.println("2. Consult complete sales history");
            System.out.println("3. Consult purchase history of a specific client");
            System.out.println("4. Consult sales attended by a specific seller");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            switch (option) {
                case 1 -> registerSale();
                case 2 -> listAllSales();
                case 3 -> listSalesByClient();
                case 4 -> listSalesBySeller();
                case 0 -> { }
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Registers a new sale transaction. Prompts for extended warranties when adding consoles.
     */
    private void registerSale() {
        try {
            System.out.println("\n--- REGISTER NEW SALE ---");

            // Validate client existence
            System.out.print("Enter Client ID: ");
            String clientId = reader.readLine();
            Client client = personService.findClientById(clientId);
            if (client == null) {
                System.out.println("Error: Client not found.");
                return;
            }

            // Validate seller existence
            System.out.print("Enter Seller Employee Code: ");
            String sellerCode = reader.readLine();
            Seller seller = personService.findSellerByCode(sellerCode);
            if (seller == null) {
                System.out.println("Error: Seller not found.");
                return;
            }

            // Add products and accessories to cart loop
            List<Product> products = new ArrayList<>();
            List<String> extendedWarrantyProductIds = new ArrayList<>();
            String addMore;
            do {
                System.out.print("Enter Product or Accessory ID to add: ");
                String itemId = reader.readLine();

                Product item = productService.findProductById(itemId);
                if (item == null) {
                    item = accessoryService.findById(itemId);
                }

                if (item != null) {
                    products.add(item);
                    System.out.println("Item added to cart.");

                    // If item is a console, ask whether to add extended warranty
                    if (item instanceof Console) {
                        System.out.print("¿Desea agregar garantía extendida (+10% del costo) a la consola '" 
                                + item.getTitle() + "'? (s/n): ");
                        String extendedChoice = reader.readLine();
                        if (extendedChoice.equalsIgnoreCase("s")) {
                            extendedWarrantyProductIds.add(item.getId());
                        }
                    }
                } else {
                    System.out.println("Error: Product or Accessory not found.");
                }

                System.out.print("Add another item? (y/n): ");
                addMore = reader.readLine();
            } while (addMore.equalsIgnoreCase("y"));

            if (products.isEmpty()) {
                System.out.println("Error: Sale aborted. At least one item is required.");
                return;
            }

            // Delegate creation to SaleService using the updated registerSale method
            Sale registeredSale = saleService.registerSale(client, seller, products, extendedWarrantyProductIds);
            System.out.println("Sale registered successfully with ID: " + registeredSale.getId());
            if (registeredSale.getAppliedPromotionName() != null) {
                System.out.println("Applied Promotion: " + registeredSale.getAppliedPromotionName() + 
                                   " | Discount: $" + registeredSale.getDiscountAmount());
            }
            System.out.println("Final Total: $" + registeredSale.getTotal());

        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error registering sale: " + e.getMessage());
        }
    }

    /**
     * Lists all recorded sales transactions.
     */
    private void listAllSales() {
        System.out.println("\n--- COMPLETE SALES HISTORY ---");
        List<Sale> allSales = saleService.getAllSales();
        printSalesList(allSales);
    }

    /**
     * Consults and prints sales history filtered by customer ID.
     */
    private void listSalesByClient() {
        try {
            System.out.print("Enter Client Identification: ");
            String searchClientId = reader.readLine();
            List<Sale> clientSales = saleService.getSalesByClient(searchClientId);
            System.out.println("\n--- CLIENT PURCHASE HISTORY ---");
            printSalesList(clientSales);
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        }
    }

    /**
     * Consults and prints sales history filtered by seller employee code.
     */
    private void listSalesBySeller() {
        try {
            System.out.print("Enter Seller Employee Code: ");
            String searchSellerId = reader.readLine();
            List<Sale> sellerSales = saleService.getSalesBySeller(searchSellerId);
            System.out.println("\n--- SELLER SALES HISTORY ---");
            printSalesList(sellerSales);
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        }
    }

    /**
     * Helper method to print formatted list of sales.
     *
     * @param sales the list of sales to format and print
     */
    private void printSalesList(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            System.out.println("No sales records found.");
        } else {
            for (Sale s : sales) {
                String promoInfo = (s.getAppliedPromotionName() != null) 
                        ? " | Promo: " + s.getAppliedPromotionName() + " (-$" + s.getDiscountAmount() + ")"
                        : "";
                
                System.out.println("------------------------------------------------------------------");
                System.out.println("Sale ID: " + s.getId() + 
                                   " | Date: " + s.getDate() + 
                                   " | Client: " + s.getClient().getName() + 
                                   " | Seller: " + s.getSeller().getName() + 
                                   promoInfo +
                                   " | Total: $" + s.getTotal());
                
                System.out.println("  Items Sold:");
                if (s.getProducts() != null && !s.getProducts().isEmpty()) {
                    for (Product item : s.getProducts()) {
                        System.out.println("    - [ID: " + item.getId() + "] " + item.getTitle() + " ($" + item.getPrice() + ")");
                    }
                } else {
                    System.out.println("    (No items listed)");
                }
            }
            System.out.println("------------------------------------------------------------------");
        }
    }

    // ================= ACCESSORY SUBMENU =================

    /**
     * Handles accessory management options loop.
     */
    private void handleAccessoryMenu() {
        int option = -1;
        do {
            System.out.println("\n============= ACCESSORY MANAGEMENT ==============");
            System.out.println("1. Register a new controller");
            System.out.println("2. Register a new cable");
            System.out.println("3. Register a new memory");
            System.out.println("4. List all accessories");
            System.out.println("5. List accessories by type");
            System.out.println("6. Consult compatible accessories for a console");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            switch (option) {
                case 1 -> registerController();
                case 2 -> registerCable();
                case 3 -> registerMemory();
                case 4 -> listAllAccessories();
                case 5 -> listAccessoriesByType();
                case 6 -> consultCompatibleAccessories();
                case 0 -> { }
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Registers a new controller accessory.
     */
    private void registerController() {
        try {
            System.out.println("\n--- REGISTER CONTROLLER ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Title: "); String title = reader.readLine();
            System.out.print("Price: "); double price = Double.parseDouble(reader.readLine());
            System.out.print("Stock: "); int stock = Integer.parseInt(reader.readLine());
            System.out.print("Connection Type (wireless/wired): "); String connection = reader.readLine();

            System.out.print("Compatible Console IDs (separated by commas, e.g., 11,12) [Leave empty if none]: ");
            String consolesInput = reader.readLine();
            List<String> compatibleConsoles = new ArrayList<>();
            if (!consolesInput.isBlank()) {
                compatibleConsoles = Arrays.asList(consolesInput.split("\\s*,\\s*"));
            }

            accessoryService.registerController(id, title, price, stock, connection, compatibleConsoles);
            System.out.println("Controller registered successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Registers a new cable accessory.
     */
    private void registerCable() {
        try {
            System.out.println("\n--- REGISTER CABLE ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Title: "); String title = reader.readLine();
            System.out.print("Price: "); double price = Double.parseDouble(reader.readLine());
            System.out.print("Stock: "); int stock = Integer.parseInt(reader.readLine());
            System.out.print("Length (meters): "); double length = Double.parseDouble(reader.readLine());
            System.out.print("Connector Type (e.g., HDMI, USB): "); String type = reader.readLine();

            System.out.print("Compatible Console IDs (separated by commas, e.g., 11,12) [Leave empty if none]: ");
            String consolesInput = reader.readLine();
            List<String> compatibleConsoles = new ArrayList<>();
            if (!consolesInput.isBlank()) {
                compatibleConsoles = Arrays.asList(consolesInput.split("\\s*,\\s*"));
            }

            accessoryService.registerCable(id, title, price, stock, length, type, compatibleConsoles);
            System.out.println("Cable registered successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Registers a new memory storage accessory.
     */
    private void registerMemory() {
        try {
            System.out.println("\n--- REGISTER MEMORY ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Title: "); String title = reader.readLine();
            System.out.print("Price: "); double price = Double.parseDouble(reader.readLine());
            System.out.print("Stock: "); int stock = Integer.parseInt(reader.readLine());
            System.out.print("Capacity (GB): "); int capacity = Integer.parseInt(reader.readLine());
            System.out.print("Memory Type (e.g., SD, microSD): "); String type = reader.readLine();
            System.out.print("Compatible Console IDs (separated by commas, e.g., 11,12) [Leave empty if none]: ");
            String consolesInput = reader.readLine();
            List<String> compatibleConsoles = new ArrayList<>();
            if (!consolesInput.isBlank()) {
                compatibleConsoles = Arrays.asList(consolesInput.split("\\s*,\\s*"));
            }

            accessoryService.registerMemory(id, title, price, stock, capacity, type, compatibleConsoles);
            System.out.println("Memory registered successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Lists all registered accessories.
     */
    private void listAllAccessories() {
        System.out.println("\n--- ALL ACCESSORIES ---");
        List<Accessory> accessories = accessoryService.listAllAccessories();
        if (accessories == null || accessories.isEmpty()) {
            System.out.println("No accessories registered.");
        } else {
            accessories.forEach(a -> System.out.println(a.getFullDescription()));
        }
    }

    /**
     * Lists accessories filtered by type.
     */
    private void listAccessoriesByType() {
        try {
            System.out.print("Enter accessory type (Controller/Cable/Memory): ");
            String type = reader.readLine();
            List<Accessory> accessories = accessoryService.listAccessoriesByType(type);
            if (accessories == null || accessories.isEmpty()) {
                System.out.println("No accessories of that type found.");
            } else {
                accessories.forEach(a -> System.out.println(a.getFullDescription()));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Lists accessories compatible with a specific console ID.
     */
    private void consultCompatibleAccessories() {
        try {
            System.out.print("Enter Console ID to check compatibility: ");
            String consoleId = reader.readLine();
            List<Accessory> accessories = accessoryService.findAccessoriesCompatibleWith(consoleId);
            if (accessories == null || accessories.isEmpty()) {
                System.out.println("No compatible accessories found.");
            } else {
                accessories.forEach(a -> System.out.println(a.getFullDescription()));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= PROMOTION SUBMENU =================

    /**
     * Handles promotion management options loop.
     */
    private void handlePromotionMenu() {
        int option = -1;
        do {
            System.out.println("\n============= PROMOTION MANAGEMENT ==============");
            System.out.println("1. Register a percentage discount promotion");
            System.out.println("2. Register a category discount promotion");
            System.out.println("3. Register a bulk purchase discount promotion");
            System.out.println("4. List all active promotions");
            System.out.println("5. List all promotions (active & inactive)");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            switch (option) {
                case 1 -> registerPercentageDiscount();
                case 2 -> registerCategoryDiscount();
                case 3 -> registerBulkPurchaseDiscount();
                case 4 -> listActivePromotions();
                case 5 -> listAllPromotions();
                case 0 -> { }
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Registers a global percentage discount promotion.
     */
    private void registerPercentageDiscount() {
        try {
            System.out.println("\n--- REGISTER PERCENTAGE DISCOUNT ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Name: "); String name = reader.readLine();
            System.out.print("Start Date (YYYY-MM-DD): "); LocalDate startDate = LocalDate.parse(reader.readLine());
            System.out.print("End Date (YYYY-MM-DD): "); LocalDate endDate = LocalDate.parse(reader.readLine());
            System.out.print("Discount Percentage (e.g., 10 or 15.5): "); double percentage = Double.parseDouble(reader.readLine());

            promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
            System.out.println("Percentage discount registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering percentage discount: " + e.getMessage());
        }
    }

    /**
     * Registers a category discount promotion.
     */
    private void registerCategoryDiscount() {
        try {
            System.out.println("\n--- REGISTER CATEGORY DISCOUNT ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Name: "); String name = reader.readLine();
            System.out.print("Start Date (YYYY-MM-DD): "); LocalDate startDate = LocalDate.parse(reader.readLine());
            System.out.print("End Date (YYYY-MM-DD): "); LocalDate endDate = LocalDate.parse(reader.readLine());
            System.out.print("Discount Percentage (e.g., 10 or 15.5): "); double percentage = Double.parseDouble(reader.readLine());

            List<String> availableCategories = new ArrayList<>();
            List<Product> products = productService.getAllProducts();

            if (products != null) {
                for (Product p : products) {
                    String category = (p.getCategory() != null) ? p.getCategory() : p.getClass().getSimpleName();
                    if (!availableCategories.contains(category)) {
                        availableCategories.add(category);
                    }
                }
            }

            System.out.println("\nAvailable categories in inventory: " + availableCategories);
            System.out.print("Enter Target Category from the list above: ");
            String category = reader.readLine();

            promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, category);
            System.out.println("Category discount registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering category discount: " + e.getMessage());
        }
    }

    /**
     * Registers a bulk purchase discount promotion.
     */
    private void registerBulkPurchaseDiscount() {
        try {
            System.out.println("\n--- REGISTER BULK PURCHASE DISCOUNT ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Name: "); String name = reader.readLine();
            System.out.print("Start Date (YYYY-MM-DD): "); LocalDate startDate = LocalDate.parse(reader.readLine());
            System.out.print("End Date (YYYY-MM-DD): "); LocalDate endDate = LocalDate.parse(reader.readLine());
            System.out.print("Minimum Quantity: "); int minQuantity = Integer.parseInt(reader.readLine());
            System.out.print("Discount Percentage (e.g., 10 or 15.5): "); double percentage = Double.parseDouble(reader.readLine());

            promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minQuantity, percentage);
            System.out.println("Bulk purchase discount registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering bulk purchase discount: " + e.getMessage());
        }
    }

    /**
     * Lists currently active promotions.
     */
    private void listActivePromotions() {
        System.out.println("\n--- ACTIVE PROMOTIONS ---");
        List<Promotion> activePromos = promotionService.listActivePromotions();

        if (activePromos == null || activePromos.isEmpty()) {
            System.out.println("No active promotions available at the moment.");
            return;
        }

        for (Promotion promo : activePromos) {
            System.out.printf("ID: %s | Name: %s | Active: %s to %s | %s%n",
                    promo.getId(),
                    promo.getName(),
                    promo.getStartDate(),
                    promo.getEndDate(),
                    promo.getDetails());
        }
    }

    /**
     * Lists all registered promotions.
     */
    private void listAllPromotions() {
        System.out.println("\n--- ALL PROMOTIONS ---");
        List<Promotion> promotions = promotionService.listAllPromotions();
        if (promotions == null || promotions.isEmpty()) {
            System.out.println("No promotions recorded.");
        } else {
            for (Promotion p : promotions) {
                System.out.println("ID: " + p.getId() + " | Name: " + p.getName() + 
                                   " | Active: " + p.getStartDate() + " to " + p.getEndDate());
            }
        }
    }

    // ================= RETURN SUBMENU =================

    /**
     * Handles return management options loop.
     */
    private void handleReturnMenu() {
        int option = -1;
        do {
            System.out.println("\n============= RETURN MANAGEMENT ==============");
            System.out.println("1. Register a new return");
            System.out.println("2. Consult all returns");
            System.out.println("3. Consult returns by client");
            System.out.println("4. Consult returns by sale");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            switch (option) {
                case 1 -> registerReturn();
                case 2 -> listAllReturns();
                case 3 -> listReturnsByClient();
                case 4 -> listReturnsBySale();
                case 0 -> { }
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Processes a new product return transaction.
     */
    private void registerReturn() {
        try {
            System.out.println("\n--- REGISTER NEW RETURN ---");
            System.out.print("Enter Sale ID: ");
            String saleId = reader.readLine();

            System.out.print("Enter Product IDs to return (comma-separated, e.g., P01,P02): ");
            String inputProducts = reader.readLine();
            List<String> productIds = Arrays.asList(inputProducts.split("\\s*,\\s*"));

            System.out.print("Enter Return Reason: ");
            String reason = reader.readLine();

            Return processedReturn = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("\nReturn processed successfully!");
            System.out.println(processedReturn.generateReturnReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error processing return: " + e.getMessage());
        }
    }

    /**
     * Helper method to print formatted lists of returns.
     *
     * @param returns the list of returns to print
     */
    private void printReturnList(List<Return> returns) {
        if (returns == null || returns.isEmpty()) {
            System.out.println("No return records found.");
        } else {
            for (Return r : returns) {
                System.out.println(r.generateReturnReceipt());
                System.out.println("----------------------------------------");
            }
        }
    }

    /**
     * Lists all recorded product returns.
     */
    private void listAllReturns() {
        System.out.println("\n--- ALL RETURNS ---");
        List<Return> returns = returnService.viewAllReturns();
        printReturnList(returns);
    }

    /**
     * Consults returns made by a specific customer.
     */
    private void listReturnsByClient() {
        try {
            System.out.print("Enter Client ID: ");
            String customerId = reader.readLine();
            List<Return> returns = returnService.viewReturnsByCustomer(customerId);
            System.out.println("\n--- RETURNS BY CLIENT ---");
            printReturnList(returns);
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        }
    }

    /**
     * Consults returns associated with a specific sale transaction.
     */
    private void listReturnsBySale() {
        try {
            System.out.print("Enter Sale ID: ");
            String saleId = reader.readLine();
            List<Return> returns = returnService.viewReturnsBySale(saleId);
            System.out.println("\n--- RETURNS BY SALE ---");
            printReturnList(returns);
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        }
    }

    /**
     * Calculates and displays net monthly financial balance (Sales minus Returns).
     */
    private void generateMonthlyBalance() {
        try {
            System.out.println("\n--- MONTHLY BALANCE REPORT ---");
            System.out.print("Enter Month (1-12): ");
            int month = Integer.parseInt(reader.readLine());
            System.out.print("Enter Year (e.g., 2026): ");
            int year = Integer.parseInt(reader.readLine());

            double netBalance = returnService.generateMonthlyBalance(month, year);
            System.out.println(String.format("\n=== MONTHLY BALANCE (%02d/%d) ===", month, year));
            System.out.println(String.format("Net Balance (Sales - Returns): $%.2f", netBalance));
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numeric values for month and year.");
        } catch (Exception e) {
            System.out.println("Error generating balance: " + e.getMessage());
        }
    }

    // ================= WARRANTY SUBMENU =================

    /**
     * Handles warranty management options loop (Requirement 4).
     */
    private void handleWarrantyMenu() {
        int option = -1;
        do {
            System.out.println("\n============= WARRANTY MANAGEMENT ==============");
            System.out.println("1. Consult warranty for a specific product in a sale");
            System.out.println("2. List all registered warranties");
            System.out.println("3. List active warranties as of today");
            System.out.println("4. List warranties expiring soon");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");

            option = readOption();
            switch (option) {
                case 1 -> consultWarrantyByProductAndSale();
                case 2 -> listAllWarranties();
                case 3 -> listActiveWarranties();
                case 4 -> listWarrantiesExpiringSoon();
                case 0 -> { }
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Consults and prints the warranty certificate associated with a product in a given sale.
     */
    private void consultWarrantyByProductAndSale() {
        try {
            System.out.println("\n--- CONSULT PRODUCT WARRANTY ---");
            System.out.print("Enter Sale ID: ");
            String saleId = reader.readLine();
            System.out.print("Enter Product ID: ");
            String productId = reader.readLine();

            if (warrantyService == null) {
                System.out.println("Error: Warranty service is not available.");
                return;
            }

            Warranty warranty = warrantyService.findWarrantyByProduct(productId, saleId);
            if (warranty != null) {
                System.out.println("\n--- WARRANTY CERTIFICATE ---");
                System.out.println(warranty.generateWarrantyCertificate());
            } else {
                System.out.println("No warranty found for Product ID '" + productId + "' in Sale ID '" + saleId + "'.");
            }
        } catch (Exception e) {
            System.out.println("Error searching warranty: " + e.getMessage());
        }
    }

    /**
     * Lists all warranties registered in the system.
     */
    private void listAllWarranties() {
        System.out.println("\n--- ALL REGISTERED WARRANTIES ---");
        if (warrantyService == null) {
            System.out.println("Error: Warranty service is not available.");
            return;
        }

        List<Warranty> warranties = warrantyService.listAllWarranties();
        printWarrantyList(warranties);
    }

    /**
     * Lists all warranties currently active on today's date.
     */
    private void listActiveWarranties() {
        System.out.println("\n--- ACTIVE WARRANTIES (TODAY) ---");
        if (warrantyService == null) {
            System.out.println("Error: Warranty service is not available.");
            return;
        }

        List<Warranty> activeWarranties = warrantyService.listActiveWarranties();
        printWarrantyList(activeWarranties);
    }

    /**
     * Prompts for threshold days and lists warranties expiring within that time frame.
     */
    private void listWarrantiesExpiringSoon() {
        try {
            System.out.println("\n--- WARRANTIES EXPIRING SOON ---");
            System.out.print("Enter number of days ahead to evaluate (e.g., 30): ");
            int daysAhead = Integer.parseInt(reader.readLine());

            if (warrantyService == null) {
                System.out.println("Error: Warranty service is not available.");
                return;
            }

            List<Warranty> expiringWarranties = warrantyService.listWarrantiesExpiringSoon(daysAhead);
            printWarrantyList(expiringWarranties);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number of days.");
        } catch (Exception e) {
            System.out.println("Error querying expiring warranties: " + e.getMessage());
        }
    }

    /**
     * Helper method to display formatted warranty certificates in console.
     *
     * @param warranties list of warranties to print
     */
    private void printWarrantyList(List<Warranty> warranties) {
        if (warranties == null || warranties.isEmpty()) {
            System.out.println("No warranties found.");
        } else {
            for (Warranty w : warranties) {
                System.out.println("------------------------------------------------------------------");
                System.out.println(w.generateWarrantyCertificate());
            }
            System.out.println("------------------------------------------------------------------");
        }
    }
}