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
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

/**
 * File-based repository for Return objects. Persists all returns in a CSV file,
 * resolving references to Sale, Product, and Accessory during loading through
 * the injected SaleService, ProductService, and AccessoryService.
 */
public class ReturnRepository {

    private static final String DEFAULT_FILE_PATH = "data/returns.csv";

    private String filePath;
    private SaleService saleService;
    private ProductService productService;
    private AccessoryService accessoryService;

    /**
     * Constructs a ReturnRepository with the default file path.
     *
     * @param saleService      service to resolve sales
     * @param productService   service to resolve products
     * @param accessoryService service to resolve accessories
     */
    public ReturnRepository(SaleService saleService, ProductService productService, AccessoryService accessoryService) {
        this(DEFAULT_FILE_PATH, saleService, productService, accessoryService);
    }

    /**
     * Constructs a ReturnRepository with a custom file path.
     *
     * @param filePath         custom path to the CSV storage file
     * @param saleService      service to resolve sales
     * @param productService   service to resolve products
     * @param accessoryService service to resolve accessories
     */
    public ReturnRepository(String filePath, SaleService saleService, ProductService productService, AccessoryService accessoryService) {
        this.filePath = filePath;
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
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
            Product product = resolveItem(productId);
            if (product != null) {
                products.add(product);
            }
        }

        return new Return(returnId, date, sale, products, reason);
    }

    /**
     * Resolves a returned item by ID, checking products first and falling back
     * to accessories, since a returned item may be either kind.
     *
     * @param itemId the identifier of the returned item
     * @return the matching Product or Accessory, or null if not found in either
     */
    private Product resolveItem(String itemId) {
        Product product = productService.findProductById(itemId);
        if (product != null) {
            return product;
        }
        return accessoryService.findById(itemId);
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
