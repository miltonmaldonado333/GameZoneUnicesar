package com.gamezone.ui;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Accessory;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.service.AccessoryService;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the interactive console user interface for the GameZone system.
 * It provides menus to execute all required operations,
 * grouping them into Product, Person, Sales, and Accessory management.
 */
public class GameZoneUI {
    
    // Service and input dependencies
    private final BufferedReader reader;
    private final SaleService saleService;
    private final ProductService productService;
    private final PersonService personService;
    private final AccessoryService accessoryService;

    // Initializes the UI with required services
    public GameZoneUI(SaleService saleService, ProductService productService, PersonService personService, AccessoryService accessoryService) {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.saleService = saleService;
        this.productService = productService;
        this.personService = personService;
        this.accessoryService = accessoryService;
    }
    
    // Starts the application main execution loop
    public void start() {
        int option = -1;
        
        System.out.println("===========================================");
        System.out.println("        WELCOME TO GAMEZONE SYSTEM         ");
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
    
    // Displays the top-level options
    private void displayMainMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println("1. Product Management (Consoles and Video Games)");
        System.out.println("2. Person Management");
        System.out.println("3. Sales Management");
        System.out.println("4. Accessory Management");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }
    
    // Reads and parses numeric user input securely
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
    
    // Routes input to the appropriate submenu handler
    private void processMainMenuOption(int option) {
        switch (option) {
            case 1 -> handleProductMenu();
            case 2 -> handlePersonMenu();
            case 3 -> handleSalesMenu();
            case 4 -> handleAccessoryMenu();
            case 0, -1 -> { }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }
    
    // ================= PRODUCT SUBMENU =================

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

    // ================= ACCESSORY SUBMENU =================

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

   private void registerController() {
        try {
            System.out.println("\n--- REGISTER CONTROLLER ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Title: "); String title = reader.readLine();
            System.out.print("Price: "); double price = Double.parseDouble(reader.readLine());
            System.out.print("Stock: "); int stock = Integer.parseInt(reader.readLine());
            System.out.print("Connection Type (wireless/wired): "); String connection = reader.readLine();
            
            // NUEVO: Pedir consolas compatibles
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

    private void registerCable() {
        try {
            System.out.println("\n--- REGISTER CABLE ---");
            System.out.print("ID: "); String id = reader.readLine();
            System.out.print("Title: "); String title = reader.readLine();
            System.out.print("Price: "); double price = Double.parseDouble(reader.readLine());
            System.out.print("Stock: "); int stock = Integer.parseInt(reader.readLine());
            System.out.print("Length (meters): "); double length = Double.parseDouble(reader.readLine());
            System.out.print("Connector Type (e.g., HDMI, USB): "); String type = reader.readLine();
            
            // NUEVO: Pedir consolas compatibles
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

    private void listAllAccessories() {
        System.out.println("\n--- ALL ACCESSORIES ---");
        List<Accessory> accessories = accessoryService.listAllAccessories();
        if (accessories == null || accessories.isEmpty()) {
            System.out.println("No accessories registered.");
        } else {
            accessories.forEach(a -> System.out.println(a.getFullDescription()));
        }
    }

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

    // ================= SALES SUBMENU =================

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
            String addMore;
            do {
                System.out.print("Enter Product or Accessory ID to add: ");
                String itemId = reader.readLine();
                
                // Buscar primero en productos regulares
                Product item = productService.findProductById(itemId);
                
                // Si no se encuentra, buscar en accesorios
                if (item == null) {
                    item = accessoryService.findById(itemId);
                }
                
                if (item != null) {
                    products.add(item);
                    System.out.println("Item added to cart.");
                } else {
                    System.out.println("Error: Product or Accessory not found.");
                }
                
                System.out.print("Add another item? (y/n): ");
                addMore = reader.readLine();
            } while (addMore.equalsIgnoreCase("y"));
            
            // Ensure cart is not empty
            if (products.isEmpty()) {
                System.out.println("Error: Sale aborted. At least one item is required.");
                return;
            }
            
            // Delegate creation to service
            Sale registeredSale = saleService.registerSale(client, seller, products);
            System.out.println("Sale registered successfully with ID: " + registeredSale.getId());

        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error registering sale: " + e.getMessage());
        }
    }

    private void listAllSales() {
        System.out.println("\n--- COMPLETE SALES HISTORY ---");
        List<Sale> allSales = saleService.getAllSales();
        printSalesList(allSales);
    }

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

    // Helper method to print formatted lists of sales
    private void printSalesList(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            System.out.println("No sales records found.");
        } else {
            for (Sale s : sales) {
                System.out.println("Sale ID: " + s.getId() + 
                                   " | Date: " + s.getDate() + 
                                   " | Client: " + s.getClient().getName() + 
                                   " | Seller: " + s.getSeller().getName() + 
                                   " | Total: $" + s.getTotal());
            }
        }
    }
}