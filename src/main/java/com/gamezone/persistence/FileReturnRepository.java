package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamezone.model.Product;
import com.gamezone.model.Return;

/**
 * File-based repository for returns. It saves each Return with the identifiers
 * of its sale and items in a CSV file and loads the stored lines as raw
 * ReturnRecord objects, so it depends on no service. The service layer rebuilds
 * the Return objects from those records.
 * Each line stores the amount refunded for cancelled warranties as its last
 * column; lines written before that column existed are loaded with zero.
 */
public class FileReturnRepository implements ReturnRepository {

    private static final String DEFAULT_FILE_PATH = "data/returns.csv";

    private String filePath;

    /**
     * Constructs a ReturnRepository with the default file path.
     */
    public FileReturnRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a ReturnRepository with a custom file path.
     *
     * @param filePath custom path to the CSV storage file
     */
    public FileReturnRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads the raw records stored in the CSV file. If the file does not exist,
     * an empty list is returned.
     *
     * @return the list of raw return records read from the file
     */
    @Override
    public List<ReturnRecord> loadAll() {
        List<ReturnRecord> records = new ArrayList<>();
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
                records.add(parseLine(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading return data: " + e.getMessage());
        }

        return records;
    }

    /**
     * Saves the given list of returns to the CSV file, overwriting its content.
     *
     * @param returns the list of returns to save
     */
    @Override
    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Return returnRecord : returns) {
                writer.write(toLine(returnRecord));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing return data: " + e.getMessage());
        }
    }

    private String toLine(Return returnRecord) {
        StringBuilder productIds = new StringBuilder();
        List<Product> products = returnRecord.getReturnedProducts();
        for (int i = 0; i < products.size(); i++) {
            productIds.append(products.get(i).getId());
            if (i < products.size() - 1) {
                productIds.append(",");
            }
        }

        return String.join(";",
                returnRecord.getReturnId(),
                returnRecord.getDate().toString(),
                String.valueOf(returnRecord.getOriginalSale().getId()),
                productIds.toString(),
                returnRecord.getReason(),
                String.valueOf(returnRecord.getRefundAmount()),
                String.valueOf(returnRecord.getWarrantyRefundAmount()));
    }

    private ReturnRecord parseLine(String line) {
        String[] fields = line.split(";", -1);
        String returnId = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        String saleId = fields[2];
        List<String> productIds = fields[3].isBlank()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(fields[3].split(",")));
        String reason = fields[4];
        double warrantyRefund = (fields.length > 6 && !fields[6].isBlank()) ? Double.parseDouble(fields[6]) : 0.0;

        return new ReturnRecord(returnId, date, saleId, productIds, reason, warrantyRefund);
    }
}