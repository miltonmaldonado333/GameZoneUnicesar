package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;

/**
 * File-based repository for Accessory objects (Controller, Cable, Memory).
 * Persists all accessories in a single CSV file using a type discriminator to
 * distinguish between the three concrete subtypes.
 */
public class AccessoryRepository {

    private String filePath;

    /**
     * Creates a repository that reads from and writes to the given file path.
     *
     * @param filePath the path of the CSV file used to store accessory data
     */
    private static final String DEFAULT_FILE_PATH = "data/accessories.csv";

    public AccessoryRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public AccessoryRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all accessories stored in the file. If the file does not exist yet,
     * an empty list is returned instead of throwing an error.
     *
     * @return the list of accessories loaded from the file
     */
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return accessories;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Accessory accessory = parseLine(line);
                if (accessory != null) {
                    accessories.add(accessory);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading accessory data: " + e.getMessage());
        }

        return accessories;
    }

    /**
     * Saves the given list of accessories to the file, overwriting any previous
     * content.
     *
     * @param accessories the list of accessories to save
     */
    public void saveAll(List<Accessory> accessories) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        // Crear la carpeta 'data' (o la ruta especificada) si no existe
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Accessory accessory : accessories) {
                writer.write(toLine(accessory));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing accessory data: " + e.getMessage());
        }
    }

    /**
     * Converts a single accessory into its CSV line representation.
     *
     * @param accessory the accessory to convert
     * @return the text line representing the accessory
     */
    private String toLine(Accessory accessory) {
        String compatibleConsoles = String.join(",", accessory.getCompatibleConsoleIds());

        if (accessory instanceof Controller controller) {
            return String.join(";",
                    "CONTROLLER",
                    controller.getId(),
                    controller.getTitle(),
                    String.valueOf(controller.getPrice()),
                    String.valueOf(controller.getStock()),
                    controller.getConnectionType(),
                    compatibleConsoles);
        } else if (accessory instanceof Cable cable) {
            return String.join(";",
                    "CABLE",
                    cable.getId(),
                    cable.getTitle(),
                    String.valueOf(cable.getPrice()),
                    String.valueOf(cable.getStock()),
                    String.valueOf(cable.getLength()),
                    cable.getConnectorType(),
                    compatibleConsoles);
        } else if (accessory instanceof Memory memory) {
            return String.join(";",
                    "MEMORY",
                    memory.getId(),
                    memory.getTitle(),
                    String.valueOf(memory.getPrice()),
                    String.valueOf(memory.getStock()),
                    String.valueOf(memory.getCapacityGB()),
                    memory.getStorageType(),
                    compatibleConsoles);
        }
        return "";
    }

    /**
     * Parses a single CSV line back into the corresponding Accessory subtype.
     *
     * @param line the text line to parse
     * @return the resulting Accessory, or null if the line has an unknown
     * format
     */
    private Accessory parseLine(String line) {
        String[] fields = line.split(";", -1);
        String type = fields[0];
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stock = Integer.parseInt(fields[4]);

        List<String> compatibleConsoles;

        if (type.equals("CONTROLLER")) {
            String connectionType = fields[5];
            compatibleConsoles = parseConsoleIds(fields[6]);
            return new Controller(compatibleConsoles, id, title, price, stock, connectionType);
        } else if (type.equals("CABLE")) {
            double length = Double.parseDouble(fields[5]);
            String connectorType = fields[6];
            compatibleConsoles = parseConsoleIds(fields[7]);
            return new Cable(compatibleConsoles, id, title, price, stock, length, connectorType);
        } else if (type.equals("MEMORY")) {
            int capacity = Integer.parseInt(fields[5]);
            String storageType = fields[6];
            compatibleConsoles = parseConsoleIds(fields[7]);
            return new Memory(capacity, storageType, id, title, price, stock, compatibleConsoles);
        }

        return null;
    }

    /**
     * Parses the comma-separated console IDs field into a list.
     *
     * @param field the raw field containing zero or more console IDs
     * @return a list of console IDs, empty if the field is blank
     */
    private List<String> parseConsoleIds(String field) {
        if (field == null || field.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(field.split(",")));
    }
}
