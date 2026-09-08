package com.gamezone.persistence;

import com.gamezone.model.Sale;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Repository class for managing Sale persistence in a text file.
 */
public class SaleRepository {
    private static final String FILE_NAME = "sales.txt";

    /**
     * Saves a sale to a text file.
     * @param sale the sale to save
     * @throws RuntimeException if there is an error writing to the file
     */
    public void save(Sale sale) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            // Nota: Aquí deberías iterar sobre la lista de productos de la venta 
            // para guardar también sus IDs en el archivo.
            writer.println(sale.getId() + ";" + sale.getDate() + ";" + sale.getCustomerName() + ";" + sale.getSellerName() + ";" + sale.getTotal());
        } catch (IOException e) {
            // Se lanza la excepción para respetar la arquitectura en capas.
            // La capa UI (el menú) deberá atrapar esto e imprimir el mensaje.
            throw new RuntimeException("Error saving sale to file: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all sales from the text file.
     * @return list of sales
     * @throws RuntimeException if there is an error reading the file
     */
    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_NAME);
        
        if (!file.exists()) {
            return sales; 
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                
                // Si decides guardar los productos, el length será mayor a 5
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    String customer = parts[2];
                    String seller = parts[3];
                    double total = Double.parseDouble(parts[4]);
                    
                    Sale sale = new Sale(id, date, customer, seller, total);
                    // Nota: Aquí deberías reconstruir la lista de productos 
                    // leyendo los IDs que guardaste y añadiéndolos al objeto Sale.
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            // Se lanza la excepción en lugar de imprimir en consola.
            throw new RuntimeException("Error reading sales from file: " + e.getMessage(), e);
        }
        
        return sales;
    }
}