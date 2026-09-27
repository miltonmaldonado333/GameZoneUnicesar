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

/**
 * File-based repository for Warranty objects (BasicWarranty, ExtendedWarranty).
 * Persists all warranties in a CSV file using a type discriminator, resolving
 * references to Sale and Product during loading through the injected
 * SaleRepository and ProductService. Depending on SaleRepository instead of
 * SaleService avoids a circular dependency, since SaleService depends on
 * WarrantyService.
 */
public class WarrantyRepository {

    private static final String DEFAULT_FILE_PATH = "data/warranties.csv";

    private String filePath;
    private SaleRepository saleRepository;
    private ProductService productService;

    /**
     * Constructs a WarrantyRepository with default file path.
     *
     * @param saleRepository repository used to resolve sales
     * @param productService service used to resolve products
     */
    public WarrantyRepository(SaleRepository saleRepository, ProductService productService) {
        this(DEFAULT_FILE_PATH, saleRepository, productService);
    }

    /**
     * Constructs a WarrantyRepository with a custom file path.
     *
     * @param filePath       custom path to the CSV storage file
     * @param saleRepository repository used to resolve sales
     * @param productService service used to resolve products
     */
    public WarrantyRepository(String filePath, SaleRepository saleRepository, ProductService productService) {
        this.filePath = filePath;
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    /**
     * Loads all warranties from the CSV storage file, reconstructing
     * relationships using the injected SaleRepository and ProductService.
     *
     * @return list of stored warranties
     */
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

    /**
     * Persists the given list of warranties into the CSV file, creating
     * parent directories if they do not exist.
     *
     * @param warranties list of warranties to persist
     */
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
        if (fields.length < 5) {
            return null;
        }

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

        if ("EXTENDED".equalsIgnoreCase(type)) {
            return new ExtendedWarranty(id, product, sale, startDate);
        } else if ("BASIC".equalsIgnoreCase(type)) {
            return new BasicWarranty(id, product, sale, startDate);
        }

        return null;
    }

    private Sale findSaleById(String saleId) {
        if (saleRepository == null) {
            return null;
        }
        for (Sale sale : saleRepository.findAll()) {
            if (String.valueOf(sale.getId()).equals(saleId)) {
                return sale;
            }
        }
        return null;
    }
}
