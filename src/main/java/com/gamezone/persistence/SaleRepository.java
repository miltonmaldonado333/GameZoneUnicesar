package com.gamezone.persistence;

import com.gamezone.model.Sale;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;  
import java.util.List;
import java.util.Scanner;

public class SaleRepository {
    private static final String FILE_NAME = "sales.txt";

    /**
     * Saves a sale to a text file.
     * @param sale the sale to save
     */
    public void save(Sale sale) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            writer.println(sale.getId() + ";" + sale.getDate() + ";" + sale.getCustomerName() + ";" + sale.getSellerName() + ";" + sale.getTotal());
        } catch (IOException e) {
            System.out.println("Error saving sale to file: " + e.getMessage());
        }
    }

    /**
     * Retrieves all sales from the text file.
     * @return list of sales
     */
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_NAME);
        
        if (!file.exists()) {
            return sales; 
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    String customer = parts[2];
                    String seller = parts[3];
                    double total = Double.parseDouble(parts[4]);
                    
                    Sale sale = new Sale(id, date, customer, seller, total);
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading sales from file: " + e.getMessage());
        }
        
        return sales;
    }
}