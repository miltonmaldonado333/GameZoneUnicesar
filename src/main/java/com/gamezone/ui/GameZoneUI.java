package com.gamezone.ui;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
//import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameZoneUI {
    //Here, i'm using private final's atributes, because it's better than just private.
    private final BufferedReader reader;
    private final SaleService saleService;
    //private final ProductService productService;

    public GameZoneUI(SaleService saleService) { //ProductService productService
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.saleService = saleService;
        //this.productService = productService;
    }
    
    public void start(){
        int option=-1;
        
        System.out.println("===========================================");
        System.out.println("        WELCOME TO GAMEZONE SYSTEM         ");
        System.out.println("===========================================");
        
        do{
          displayMainMenu();
          option=readOption();
          processMainMenuOption(option);
        }while(option != 0);
        System.out.println("Exiting GameZone system... Goodbye!");
        
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing reader: " + e.getMessage());
        }
    }
    
  //This is the main menu, where employees can pick the option the user wants.
    private void displayMainMenu(){
      System.out.println("\n---------------- MAIN MENU ----------------");
      System.out.println("1. Product Management");
      System.out.println("2. Person Management");
      System.out.println("3. Sales Management");
      System.out.println("0. Exit");
      System.out.print("Select an option: ");
    }
    
    //here the errors are being controlled through exceptions
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
   
   //This is the process main menu,This menu leads the menu to the submenus of products, people, and sales.
   private void processMainMenuOption(int option) {
        switch (option) {
            case 1:
                handleProductMenu();
                break;
            case 2:
                handlePersonMenu();
                break;
            case 3:
                handleSalesMenu();
                break;
            case 0:
                break;
            case -1:
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }
   
   // ================= PRODUCT SUBMENU (Operations 1-3) =================
   private void handleProductMenu(){
       int option=-1;
       System.out.println("============= PRODUCT MANGEMENT ==============");
       System.out.println("1.Register a new videogame");
       System.out.println("2.Register a new console");
       System.out.println("3.List all avaaliable products in inventory");
       System.out.println("Select an option: ");
       option=readOption();
       switch (option) {
                case 1:
                    System.out.println("[Feature] Register Video Game (Pending integration with Developer 1)");
                    break;
                case 2:
                    System.out.println("[Feature] Register Console (Pending integration with Developer 1)");
                    break;
                case 3:
                    System.out.println("[Feature] List Products (Pending integration with Developer 1)");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
       }
       
   }
   
   private void handlePersonMenu(){
       
   }
   
   private void handleSalesMenu() {
        int option = -1;
        do {
            System.out.println("\n--- SALES MANAGEMENT ---");
            System.out.println("7. Register a new sale[cite: 1]");
            System.out.println("8. Consult complete sales history[cite: 1]");
            System.out.println("9. Consult purchase history of a specific client[cite: 1]");
            System.out.println("10. Consult sales attended by a specific seller[cite: 1]");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select an option: ");
            
            option = readOption();
            try {
                switch (option) {
                    case 7:
                        System.out.print("Enter Sale ID (number): ");
                        int saleId = Integer.parseInt(reader.readLine());
                        
                        System.out.print("Enter Date (YYYY-MM-DD): ");
                        String date = reader.readLine();
                        
                        System.out.print("Enter Client ID: ");
                        String clientId = reader.readLine();
                        Client client = new Client();
                        client.setId(clientId);
                        
                        System.out.print("Enter Seller ID: ");
                        String sellerId = reader.readLine();
                        Seller seller = new Seller();
                        seller.setId(sellerId);
                        
                        List<Product> products = new ArrayList<>();
                        String addMore;
                        do {
                            System.out.print("Enter Product ID to add: ");
                            String prodId = reader.readLine();
                            System.out.print("Enter Product Price: ");
                            double prodPrice = Double.parseDouble(reader.readLine());
                            
                            Product p = new Product() {
                                @Override
                                public String getFullDescription() {
                                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                                }
                            };
                            p.setId(prodId);
                            p.setPrice(prodPrice);
                            p.setStock(10); 
                            products.add(p);
                            
                            System.out.print("Add another product? (y/n): ");
                            addMore = reader.readLine();
                        } while (addMore.equalsIgnoreCase("y"));
                        
                        Sale newSale = new Sale(saleId, date, client, seller, products);
                        saleService.registerSale(newSale);
                        System.out.println("Sale registered successfully!");
                        break;
                        
                    case 8:
                        List<Sale> allSales = saleService.getAllSales();
                        System.out.println("\n--- COMPLETE SALES HISTORY ---");
                        if (allSales.isEmpty()) {
                            System.out.println("No sales recorded yet.");
                        } else {
                            for (Sale s : allSales) {
                                System.out.println("ID: " + s.getId() + " | Date: " + s.getDate() + " | Total: $" + s.getTotal());
                            }
                        }
                        break;
                        
                    case 9:
                        System.out.print("Enter Client ID: ");
                        String searchClientId = reader.readLine();
                        List<Sale> clientSales = saleService.getSalesByClient(searchClientId);
                        System.out.println("\n--- CLIENT PURCHASE HISTORY ---");
                        for (Sale s : clientSales) {
                            System.out.println("Sale ID: " + s.getId() + " | Total: $" + s.getTotal());
                        }
                        break;
                        
                    case 10:
                        System.out.print("Enter Seller ID: ");
                        String searchSellerId = reader.readLine();
                        List<Sale> sellerSales = saleService.getSalesBySeller(searchSellerId);
                        System.out.println("\n--- SELLER SALES HISTORY ---");
                        for (Sale s : sellerSales) {
                            System.out.println("Sale ID: " + s.getId() + " | Total: $" + s.getTotal());
                        }
                        break;
                        
                    case 0:
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error processing sale operation: " + e.getMessage());
            }
        } while (option != 0);
    }
}