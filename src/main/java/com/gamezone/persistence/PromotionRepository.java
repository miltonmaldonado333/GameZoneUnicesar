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

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

/**
 * File-based repository for persisting and retrieving {@link Promotion} instances.
 * <p>
 * Handles serialization and deserialization of promotion records to and from
 * a CSV file using a discriminator field to distinguish concrete promotion types.
 * </p>
 */
public class PromotionRepository {

    /** Default file path for storing promotion data. */
    private static final String DEFAULT_FILE_PATH = "data/promotions.csv";

    /** Path to the target CSV persistence file. */
    private final String filePath;

    /**
     * Constructs a {@code PromotionRepository} using the default file path.
     */
    public PromotionRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a {@code PromotionRepository} with a custom file path.
     *
     * @param filePath the file path where promotion data is saved and loaded
     */
    public PromotionRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all persisted promotion records from the CSV file.
     *
     * @return a list of active and inactive {@link Promotion} objects, or an empty list if the file does not exist
     */
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return promotions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Promotion promotion = parseLine(line);
                if (promotion != null) {
                    promotions.add(promotion);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading promotion data: " + e.getMessage());
        }

        return promotions;
    }

    /**
     * Overwrites the CSV file with the current list of promotion objects.
     *
     * @param promotions the list of {@link Promotion} instances to persist
     */
    public void saveAll(List<Promotion> promotions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Promotion promotion : promotions) {
                writer.write(toLine(promotion));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing promotion data: " + e.getMessage());
        }
    }

    /**
     * Serializes a {@link Promotion} instance into a formatted CSV record.
     *
     * @param promotion the promotion entity to format
     * @return a semi-colon delimited string representing the promotion record
     */
    private String toLine(Promotion promotion) {
        if (promotion instanceof PercentageDiscount) {
            PercentageDiscount pd = (PercentageDiscount) promotion;
            return String.join(";",
                    "PERCENTAGE",
                    pd.getId(),
                    pd.getName(),
                    pd.getStartDate().toString(),
                    pd.getEndDate().toString(),
                    String.valueOf(pd.getPercentage()));
        } else if (promotion instanceof CategoryDiscount) {
            CategoryDiscount cd = (CategoryDiscount) promotion;
            return String.join(";",
                    "CATEGORY",
                    cd.getId(),
                    cd.getName(),
                    cd.getStartDate().toString(),
                    cd.getEndDate().toString(),
                    String.valueOf(cd.getPercentage()),
                    cd.getTargetCategory());
        } else if (promotion instanceof BulkPurchaseDiscount) {
            BulkPurchaseDiscount bd = (BulkPurchaseDiscount) promotion;
            return String.join(";",
                    "BULK",
                    bd.getId(),
                    bd.getName(),
                    bd.getStartDate().toString(),
                    bd.getEndDate().toString(),
                    String.valueOf(bd.getMinQuantity()),
                    String.valueOf(bd.getPercentage()));
        }
        return "";
    }

    /**
     * Deserializes a single line from the CSV file into a concrete {@link Promotion} object.
     *
     * @param line a semi-colon delimited string from the data file
     * @return a reconstructed {@link Promotion} instance, or {@code null} if parsing fails
     */
    private Promotion parseLine(String line) {
        try {
            String[] fields = line.split(";", -1);
            if (fields.length < 6) {
                return null;
            }

            String type = fields[0];
            String id = fields[1];
            String name = fields[2];
            LocalDate startDate = LocalDate.parse(fields[3]);
            LocalDate endDate = LocalDate.parse(fields[4]);

            if ("PERCENTAGE".equalsIgnoreCase(type)) {
                double percentage = Double.parseDouble(fields[5]);
                return new PercentageDiscount(percentage, id, name, startDate, endDate);
            } else if ("CATEGORY".equalsIgnoreCase(type)) {
                double percentage = Double.parseDouble(fields[5]);
                String targetCategory = fields[6];
                return new CategoryDiscount(percentage, targetCategory, id, name, startDate, endDate);
            } else if ("BULK".equalsIgnoreCase(type)) {
                int minQuantity = Integer.parseInt(fields[5]);
                double percentage = Double.parseDouble(fields[6]);
                return new BulkPurchaseDiscount(minQuantity, percentage, id, name, startDate, endDate);
            }
        } catch (Exception e) {
            System.out.println("Error parsing line: " + line + " -> " + e.getMessage());
        }

        return null;
    }
}