package com.gamezone.ui;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the interactive console user interface for the GameZone system.
 * It provides menus to execute all required operations,
 * grouping them into Product, Person, and Sales management.
 */
public class GameZoneUI {
    
    // Service and input dependencies
    private final BufferedReader reader;
    private final SaleService saleService;
    private final ProductService productService;
    private final PersonService personService;

    // Initializes the UI with required services
    public GameZoneUI(SaleService saleService, ProductService productService, PersonService personService) {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.saleService = saleService;
        this.productService = productService;
        this.personService = personService;
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
        System.out.println("1. Product Management");
        System.out.println("2. Person Management");
        System.out.println("3. Sales Management");
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
            case 0, -1 -> { }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }
    
    // ================= PRODUCT SUBMENU =================

    private void handleProductMenu() {
        int option = -1;
        do {
            System.out.println("\n============= PRODUCT MANAGEMENT ==============");
            System.out.println("1. Register a new videogame");
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

    // Collects data and registers a new video game
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

    // Collects data and registers a new console
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

    // Prints the full inventory list
    private void listProducts() {
        System.out.println("\n--- INVENTORY ---");
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

    // Registers a new client entity
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

    // Registers a new seller entity
    private void registerSeller() {
        try {
            System.out.println("\n--- REGISTER SELLER ---");
            System.out.print("Name: "); String name = reader.readLine();
            System.out.print("Employee Code: "); String employeeCode = reader.readLine();
            System.out.print("Work Shift: "); String workShift = reader.readLine();

            personService.registerSeller(name, employeeCode, workShift);
            System.out.println("Seller registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering seller: " + e.getMessage());
        }
    }

    // Prints all registered clients
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

    // Prints all registered sellers
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

    // Handles the complete workflow of registering a new sale
    private void registerSale() {
        try {
            System.out.println("\n--- REGISTER NEW SALE ---");
            System.out.print("Enter Sale ID (integer): ");
            int saleId = Integer.parseInt(reader.readLine());
            
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
            
            // Add products to cart loop
            List<Product> products = new ArrayList<>();
            String addMore;
            do {
                System.out.print("Enter Product ID to add: ");
                String prodId = reader.readLine();
                Product p = productService.findProductById(prodId);
                
                if (p != null) {
                    products.add(p);
                    System.out.println("Product added to cart.");
                } else {
                    System.out.println("Error: Product not found.");
                }
                
                System.out.print("Add another product? (y/n): ");
                addMore = reader.readLine();
            } while (addMore.equalsIgnoreCase("y"));
            
            // Ensure cart is not empty
            if (products.isEmpty()) {
                System.out.println("Error: Sale aborted. At least one product is required.");
                return;
            }
            
            String date = LocalDate.now().toString();
            Sale newSale = new Sale(saleId, date, client, seller, products);
            
            saleService.registerSale(newSale);
            System.out.println("Sale registered successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid numeric input.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error registering sale: " + e.getMessage());
        }
    }

    // Lists all recorded sales in the system
    private void listAllSales() {
        System.out.println("\n--- COMPLETE SALES HISTORY ---");
        List<Sale> allSales = saleService.getAllSales();
        printSalesList(allSales);
    }

    // Lists sales associated with a specific client ID
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

    // Lists sales attended by a specific seller code
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