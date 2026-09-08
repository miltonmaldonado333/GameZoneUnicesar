package com.gamezone.ui;
import com.gamezone.model.Sale;
//import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class GameZoneUI {
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
          processMainMenuOption();
        }while(option != 0);
        System.out.println("Exiting GameZone system... Goodbye!");
        
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing reader: " + e.getMessage());
        }
    }
    
    private void displayMainMenu(){
      System.out.println("\n---------------- MAIN MENU ----------------");
      System.out.println("1. Product Management");
      System.out.println("2. Person Management");
      System.out.println("3. Sales Management");
      System.out.println("0. Exit");
      System.out.print("Select an option: ");
    }
    
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
       
   }
    
    
}
