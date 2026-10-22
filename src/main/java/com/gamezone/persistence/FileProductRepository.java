package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of {@link ProductRepository} using semicolon-separated values.
 */
public class FileProductRepository implements ProductRepository {

    private final String filePath;

    public FileProductRepository(String filePath) {
        this.filePath = filePath;
    }

    public FileProductRepository() {
        this.filePath = "products.txt";
    }

    @Override
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return products;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length < 8) {
                    continue; // Omite líneas con formato incompleto
                }

                String type = parts[0].trim();

                if (type.equalsIgnoreCase("Console")) {
                    // Estructura esperada: Console;brand;model;generation;id;title;price;stock
                    String brand = parts[1].trim();
                    String model = parts[2].trim();
                    String generation = parts[3].trim();
                    String id = parts[4].trim();
                    String title = parts[5].trim();
                    double price = Double.parseDouble(parts[6].trim());
                    int stock = Integer.parseInt(parts[7].trim());

                    products.add(new Console(brand, model, generation, id, title, price, stock));

                } else if (type.equalsIgnoreCase("VideoGame")) {
                    // Estructura esperada: VideoGame;platform;genre;ageRating;id;title;price;stock
                    String platform = parts[1].trim();
                    String genre = parts[2].trim();
                    String ageRating = parts[3].trim();
                    String id = parts[4].trim();
                    String title = parts[5].trim();
                    double price = Double.parseDouble(parts[6].trim());
                    int stock = Integer.parseInt(parts[7].trim());

                    products.add(new VideoGame(platform, genre, ageRating, id, title, price, stock));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error procesando el archivo de productos: " + e.getMessage());
        }

        return products;
    }

    @Override
    public void saveProducts(List<Product> products) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Product p : products) {
                if (p instanceof Console c) {
                    // Guarda con discriminador Console y sus campos específicos
                    pw.printf("Console;%s;%s;%s;%s;%s;%.2f;%d%n",
                            c.getBrand(), c.getModel(), c.getGeneration(),
                            c.getId(), c.getTitle(), c.getPrice(), c.getStock());
                } else if (p instanceof VideoGame g) {
                    // Guarda con discriminador VideoGame y sus campos específicos
                    pw.printf("VideoGame;%s;%s;%s;%s;%s;%.2f;%d%n",
                            g.getPlatform(), g.getGenre(), g.getAgeRating(),
                            g.getId(), g.getTitle(), g.getPrice(), g.getStock());
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando productos en archivo: " + e.getMessage());
        }
    }
}