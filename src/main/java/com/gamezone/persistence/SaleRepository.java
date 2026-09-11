package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles text file persistence operations for sales transactions.
 */
public class SaleRepository {
    // File name used for storing sales records
    private static final String FILE_NAME = "sales.txt";
    private PersonService personService;
    private ProductService productService;

    /**
     * Constructs a SaleRepository linked with the required person and product services
     * to reconstruct sale associations.
     * 
     * @param personService service used to look up clients and sellers
     * @param productService service used to look up purchased products
     */
    public SaleRepository(PersonService personService, ProductService productService) {
        this.personService = personService;
        this.productService = productService;
    }

    /**
     * Appends a new sale record to the text file using semicolon-separated IDs.
     * 
     * @param sale the sale object to be saved
     */
    public void save(Sale sale) {
        // Open file writer in append mode
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            StringBuilder sb = new StringBuilder();
            
            // Format: id;date;client_id;seller_code;product_id_1;product_id_2...
            sb.append(sale.getId()).append(";")
              .append(sale.getDate()).append(";")
              .append(sale.getClient().getIdentification()).append(";")
              .append(sale.getSeller().getEmployeeCode());
            
            // Append all product IDs linked to this sale
            for (Product p : sale.getProducts()) {
                sb.append(";").append(p.getId());
            }
            
            writer.println(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error saving sale to file: " + e.getMessage(), e);
        }
    }

    /**
     * Reads all recorded sales from the text file and rebuilds the corresponding objects.
     * 
     * @return a list containing all successfully parsed sales
     */
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_NAME);
        
        // Return an empty list if the persistence file doesn't exist yet
        if (!file.exists()) {
            return sales; 
        }

        // Read records line by line using a scanner
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) continue;
                
                String[] parts = line.split(";");
                
                // Ensure the line contains at least the base sale fields plus one product
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    
                    // Rebuild relationships using their respective service lookups
                    Client client = personService.findClientById(parts[2]);
                    Seller seller = personService.findSellerByCode(parts[3]);
                    
                    List<Product> products = new ArrayList<>();
                    for (int i = 4; i < parts.length; i++) {
                        Product p = productService.findProductById(parts[i]);
                        if (p != null) {
                            products.add(p);
                        }
                    }
                    
                    // Add the sale only if all core entities are valid and products were found
                    if (client != null && seller != null && !products.isEmpty()) {
                        Sale sale = new Sale(id, date, client, seller, products);
                        sales.add(sale);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading sales from file: " + e.getMessage(), e);
        }
        
        return sales;
    }
}