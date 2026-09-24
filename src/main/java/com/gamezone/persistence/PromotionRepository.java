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
 * File-based repository for Promotion objects (PercentageDiscount, CategoryDiscount,
 * BulkPurchaseDiscount). Persists all promotions in a single CSV file using a type
 * discriminator to distinguish between the three concrete subtypes.
 */
public class PromotionRepository {

    private static final String DEFAULT_FILE_PATH = "data/promotions.csv";

    private String filePath;

    public PromotionRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public PromotionRepository(String filePath) {
        this.filePath = filePath;
    }

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

    private String toLine(Promotion promotion) {
        if (promotion instanceof PercentageDiscount percentageDiscount) {
            return String.join(";",
                    "PERCENTAGE",
                    percentageDiscount.getId(),
                    percentageDiscount.getName(),
                    percentageDiscount.getStartDate().toString(),
                    percentageDiscount.getEndDate().toString(),
                    String.valueOf(percentageDiscount.getPercentage()));
        } else if (promotion instanceof CategoryDiscount categoryDiscount) {
            return String.join(";",
                    "CATEGORY",
                    categoryDiscount.getId(),
                    categoryDiscount.getName(),
                    categoryDiscount.getStartDate().toString(),
                    categoryDiscount.getEndDate().toString(),
                    String.valueOf(categoryDiscount.getPercentage()),
                    categoryDiscount.getTargetCategory());
        } else if (promotion instanceof BulkPurchaseDiscount bulkPurchaseDiscount) {
            return String.join(";",
                    "BULK",
                    bulkPurchaseDiscount.getId(),
                    bulkPurchaseDiscount.getName(),
                    bulkPurchaseDiscount.getStartDate().toString(),
                    bulkPurchaseDiscount.getEndDate().toString(),
                    String.valueOf(bulkPurchaseDiscount.getMinQuantity()),
                    String.valueOf(bulkPurchaseDiscount.getPercentage()));
        }
        return "";
    }

    private Promotion parseLine(String line) {
        String[] fields = line.split(";", -1);
        String type = fields[0];
        String id = fields[1];
        String name = fields[2];
        LocalDate startDate = LocalDate.parse(fields[3]);
        LocalDate endDate = LocalDate.parse(fields[4]);

        if (type.equals("PERCENTAGE")) {
            double percentage = Double.parseDouble(fields[5]);
            return new PercentageDiscount(percentage, id, name, startDate, endDate);
        } else if (type.equals("CATEGORY")) {
            double percentage = Double.parseDouble(fields[5]);
            String targetCategory = fields[6];
            return new CategoryDiscount(percentage, targetCategory, id, name, startDate, endDate);
        } else if (type.equals("BULK")) {
            int minQuantity = Integer.parseInt(fields[5]);
            double percentage = Double.parseDouble(fields[6]);
            return new BulkPurchaseDiscount(minQuantity, percentage, id, name, startDate, endDate);
        }

        return null;
    }
}
