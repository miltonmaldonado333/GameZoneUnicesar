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

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

/**
 * File-based repository for Return objects. Persists all returns in a CSV file,
 * resolving references to Sale and Product during loading through the injected
 * SaleService and ProductService.
 */
public class ReturnRepository {

    private static final String DEFAULT_FILE_PATH = "data/returns.csv";

    private String filePath;
    private SaleService saleService;
    private ProductService productService;

    public ReturnRepository(SaleService saleService, ProductService productService) {
        this(DEFAULT_FILE_PATH, saleService, productService);
    }

    public ReturnRepository(String filePath, SaleService saleService, ProductService productService) {
        this.filePath = filePath;
        this.saleService = saleService;
        this.productService = productService;
    }

    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return returns;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Return returnRecord = parseLine(line);
                if (returnRecord != null) {
                    returns.add(returnRecord);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading return data: " + e.getMessage());
        }

        return returns;
    }

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
                String.valueOf(returnRecord.getRefundAmount()));
    }

    private Return parseLine(String line) {
        String[] fields = line.split(";", -1);
        String returnId = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        String saleId = fields[2];
        String[] productIdArray = fields[3].isBlank() ? new String[0] : fields[3].split(",");
        String reason = fields[4];

        Sale sale = findSaleById(saleId);
        if (sale == null) {
            return null;
        }

        List<Product> products = new ArrayList<>();
        for (String productId : productIdArray) {
            Product product = productService.findProductById(productId);
            if (product != null) {
                products.add(product);
            }
        }

        return new Return(returnId, date, sale, products, reason);
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
