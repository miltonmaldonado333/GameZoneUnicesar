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

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

/**
 * File-based repository for Warranty objects (BasicWarranty, ExtendedWarranty).
 * Persists all warranties in a CSV file using a type discriminator, resolving
 * references to Sale and Product during loading through the injected services.
 */
public class WarrantyRepository {

    private static final String DEFAULT_FILE_PATH = "data/warranties.csv";

    private String filePath;
    private SaleService saleService;
    private ProductService productService;

    public WarrantyRepository(SaleService saleService, ProductService productService) {
        this(DEFAULT_FILE_PATH, saleService, productService);
    }

    public WarrantyRepository(String filePath, SaleService saleService, ProductService productService) {
        this.filePath = filePath;
        this.saleService = saleService;
        this.productService = productService;
    }

    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return warranties;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Warranty warranty = parseLine(line);
                if (warranty != null) {
                    warranties.add(warranty);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading warranty data: " + e.getMessage());
        }

        return warranties;
    }

    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Warranty warranty : warranties) {
                writer.write(toLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing warranty data: " + e.getMessage());
        }
    }

    private String toLine(Warranty warranty) {
        String type = (warranty instanceof ExtendedWarranty) ? "EXTENDED" : "BASIC";

        return String.join(";",
                type,
                warranty.getId(),
                warranty.getProduct().getId(),
                String.valueOf(warranty.getSale().getId()),
                warranty.getStartDate().toString());
    }

    private Warranty parseLine(String line) {
        String[] fields = line.split(";", -1);
        String type = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleId = fields[3];
        LocalDate startDate = LocalDate.parse(fields[4]);

        Product product = productService.findProductById(productId);
        Sale sale = findSaleById(saleId);

        if (product == null || sale == null) {
            return null;
        }

        if (type.equals("EXTENDED")) {
            return new ExtendedWarranty(id, product, sale, startDate);
        } else if (type.equals("BASIC")) {
            return new BasicWarranty(id, product, sale, startDate);
        }

        return null;
    }

    private Sale findSaleById(String saleId) {
        for (Sale sale : saleService.getAllSales()) {
            if (String.valueOf(sale.getId()).equals(saleId)) {
                return sale;
            }
        }
        return null;
    }
}
