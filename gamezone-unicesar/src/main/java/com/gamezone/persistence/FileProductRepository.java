package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of ProductRepository that handles file-based
 * persistence.
 *
 * @author Jesus
 */
public class FileProductRepository implements ProductRepository {

    private static final String FILE_PATH = "products.txt";

    @Override
    // Reads products from the file and returns them in a list
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList();
        File file = new File(FILE_PATH);
        // If the file does not exist, return an empty list
        if (!file.exists()) {
            return products;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String data[] = line.split(";");
                String type = data[0];
                if (type.equalsIgnoreCase("VideoGame")) {
                    String platform = data[1];
                    String genre = data[2];
                    String ageRating = data[3];
                    String id = data[4];
                    String title = data[5];
                    double price = Double.parseDouble(data[6]);
                    int stock = Integer.parseInt(data[7]);
                    products.add(new VideoGame(platform, genre, ageRating, id, title, price, stock));
                } else if (type.equalsIgnoreCase("Console")) {
                    String brand = data[1];
                    String model = data[2];
                    String generation = data[3];
                    String id = data[4];
                    String title = data[5];
                    double price = Double.parseDouble(data[6]);
                    int stock = Integer.parseInt(data[7]);
                    products.add(new Console(brand, model, generation, id, title, price, stock));
                }

            }
        } catch (IOException e) {
            System.err.println("Error reading products file: " + e.getMessage());
        }
        return products;

    }

    // Overwrites the file with the current list of products
    @Override
    public void saveProducts(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Product product : products) {
                if (product instanceof VideoGame) {
                    VideoGame game = (VideoGame) product;
                    writer.write(String.format("VideoGame;%s;%s;%s;%s;%s;%.2f;%d", game.getPlatform(), game.getGenre(), game.getAgeRating(), game.getId(), game.getTitle(), game.getPrice(), game.getStock()));

                } else if (product instanceof Console) {
                    Console console = (Console) product;
                    writer.write(String.format("Console;%s;%s;%s;%s;%s;%.2f;%d", console.getBrand(), console.getModel(), console.getGeneration(), console.getId(), console.getTitle(), console.getPrice(), console.getStock()));

                }
                writer.newLine();

            }
        } catch (IOException ex) {
            System.err.println("Error writing products file: " + ex.getMessage());
        }

    }

}
