package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Concrete implementation of ProductRepository handling file-based persistence.
 * Any IOException raised while reading or writing the products file is wrapped
 * in a PersistenceException that names the file and the failed operation.
 */
public class FileProductRepository implements ProductRepository {

    private static final String FILE_NAME = "products.txt";
    private static final String FILE_PATH = System.getProperty("user.dir") + java.io.File.separator + FILE_NAME;

    /**
     * Loads all products from the products file.
     *
     * @return the list of products read from the file
     * @throws PersistenceException if the file cannot be read
     */
    @Override
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return products;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(";");
                String type = data[0];

                if (type.equalsIgnoreCase("VideoGame")) {
                    String platform = data[1];
                    String genre = data[2];
                    String ageRating = data[3];
                    String id = data[4];
                    String title = data[5];
                    // Normaliza la coma a punto para evitar NumberFormatException
                    double price = Double.parseDouble(data[6].replace(",", "."));
                    int stock = Integer.parseInt(data[7]);
                    products.add(new VideoGame(platform, genre, ageRating, id, title, price, stock));

                } else if (type.equalsIgnoreCase("Console")) {
                    String brand = data[1];
                    String model = data[2];
                    String generation = data[3];
                    String id = data[4];
                    String title = data[5];
                    // Normaliza la coma a punto para evitar NumberFormatException
                    double price = Double.parseDouble(data[6].replace(",", "."));
                    int stock = Integer.parseInt(data[7]);
                    products.add(new Console(brand, model, generation, id, title, price, stock));
                }
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error al leer el archivo '" + FILE_NAME + "' (operacion: lectura de productos): " + e.getMessage(), e);
        }

        return products;
    }

    /**
     * Saves the given products to the products file, overwriting its content.
     *
     * @param products the list of products to save
     * @throws PersistenceException if the file cannot be written
     */
    @Override
    public void saveProducts(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Product product : products) {
                // Forzar Locale.US para asegurar que el decimal sea un punto (.)
                if (product instanceof VideoGame game) {
                    writer.write(String.format(Locale.US, "VideoGame;%s;%s;%s;%s;%s;%.2f;%d",
                            game.getPlatform(), game.getGenre(), game.getAgeRating(),
                            game.getId(), game.getTitle(), game.getPrice(), game.getStock()));

                } else if (product instanceof Console console) {
                    writer.write(String.format(Locale.US, "Console;%s;%s;%s;%s;%s;%.2f;%d",
                            console.getBrand(), console.getModel(), console.getGeneration(),
                            console.getId(), console.getTitle(), console.getPrice(), console.getStock()));
                }
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new PersistenceException(
                    "Error al escribir el archivo '" + FILE_NAME + "' (operacion: guardado de productos): " + ex.getMessage(), ex);
        }
    }
}