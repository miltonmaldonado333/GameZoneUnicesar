package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Warranty;

/**
 * File-based repository for warranties. It saves each Warranty with the
 * identifiers of its product and sale in a CSV file, using a type discriminator,
 * and loads the stored lines as raw WarrantyRecord objects, so it depends on no
 * service. The service layer rebuilds the Warranty objects from those records.
 */
public class FileWarrantyRepository implements WarrantyRepository {

    private static final String DEFAULT_FILE_PATH = "data/warranties.csv";

    private String filePath;
    private final WarrantyTypeRegistry types;

    /**
     * Constructs a WarrantyRepository with the default file path.
     */
    public FileWarrantyRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a WarrantyRepository with a custom file path.
     *
     * @param filePath custom path to the CSV storage file
     */
    public FileWarrantyRepository(String filePath) {
        this(filePath, WarrantyTypeRegistry.withDefaults());
    }

    /**
     * Constructs a WarrantyRepository with a custom file path and the given types.
     *
     * @param filePath custom path to the CSV storage file
     * @param types    the registry with the stored type of each warranty class
     */
    public FileWarrantyRepository(String filePath, WarrantyTypeRegistry types) {
        this.filePath = filePath;
        this.types = types;
    }

    /**
     * Loads the raw records stored in the CSV file. If the file does not exist,
     * an empty list is returned. Lines with fewer than five fields are skipped.
     *
     * @return the list of raw warranty records read from the file
     */
    @Override
    public List<WarrantyRecord> loadAll() {
        List<WarrantyRecord> records = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                WarrantyRecord stored = parseLine(line);
                if (stored != null) {
                    records.add(stored);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading warranty data: " + e.getMessage());
        }

        return records;
    }

    /**
     * Persists the given list of warranties into the CSV file, creating
     * parent directories if they do not exist.
     *
     * @param warranties list of warranties to persist
     */
    @Override
    public void saveAll(List<Warranty> warranties) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Warranty warranty : warranties) {
                writer.write(toLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing warranty data: " + e.getMessage());
        }
    }

    private String toLine(Warranty warranty) {
        String type = types.typeOf(warranty);

        return String.join(";",
                type,
                warranty.getId(),
                warranty.getProduct().getId(),
                String.valueOf(warranty.getSale().getId()),
                warranty.getStartDate().toString());
    }

    private WarrantyRecord parseLine(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length < 5) {
            return null;
        }

        return new WarrantyRecord(fields[0], fields[1], fields[2], fields[3], LocalDate.parse(fields[4]));
    }
}