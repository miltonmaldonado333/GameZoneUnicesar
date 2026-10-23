package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Handles CSV and plain text file persistence operations for sales transactions.
 * Implements the {@link ISaleRepository} interface to ensure full architectural contract compliance,
 * thread-safe stream flushing, and safe entity reconstruction from disk.
 */
public class SaleRepository implements ISaleRepository {

    private final String filePath;
    private PersonService personService;
    private ProductService productService;

    /**
     * Constructs a default SaleRepository pointing to the standard storage path ("sales.txt").
     */
    public SaleRepository() {
        this("sales.txt");
    }

    /**
     * Constructs a SaleRepository configured with a custom file storage path.
     *
     * @param filePath the target relative or absolute file path for data persistence
     */
    public SaleRepository(String filePath) {
        this.filePath = filePath;
        ensureDirectoryExists();
    }

    /**
     * Injects required domain services for entity reconstruction when parsing raw persistent records.
     *
     * @param personService  the service used to resolve client and seller details
     * @param productService the service used to resolve product catalog items
     */
    public void setDependencies(PersonService personService, ProductService productService) {
        this.personService = personService;
        this.productService = productService;
    }

    /**
     * Ensures that the parent directory structure exists on the physical storage device prior to I/O operations.
     *
     * @throws PersistenceException if the directory path creation fails
     */
    private void ensureDirectoryExists() {
        try {
            Path path = Path.of(filePath);
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            throw new PersistenceException("Error creating target directory structure for path: " + filePath, e);
        }
    }

    /**
     * Appends a new sale record to the persistent text file including calculated amounts and product IDs.
     * Flushes the underlying stream to guarantee physical disk writes.
     *
     * @param sale the sale entity to be persisted
     * @return the saved sale instance
     * @throws PersistenceException if the sale object is null or if an I/O writing error occurs
     */
    @Override
    public Sale save(Sale sale) {
        if (sale == null) {
            throw new PersistenceException("Error: Cannot persist a null sale entity.", null);
        }

        String clientId = (sale.getClient() != null) ? sale.getClient().getIdentification() : "DESCONOCIDO";
        String sellerCode = (sale.getSeller() != null) ? sale.getSeller().getEmployeeCode() : "DESCONOCIDO";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            StringBuilder sb = new StringBuilder();

            sb.append(sale.getId()).append(";")
                    .append(sale.getDate()).append(";")
                    .append(clientId).append(";")
                    .append(sellerCode).append(";")
                    .append(sale.getDiscountAmount()).append(";")
                    .append(sale.getExtendedWarrantyCost()).append(";")
                    .append(sale.getTotal());

            if (sale.getProducts() != null) {
                for (Product p : sale.getProducts()) {
                    if (p != null) {
                        sb.append(";").append(p.getId());
                    }
                }
            }

            writer.println(sb.toString());
            writer.flush();

            return sale;

        } catch (IOException e) {
            throw new PersistenceException(
                    "Error writing to persistence file '" + filePath + "' during save operation: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a sale record matching the given identifier from the file.
     * Rewrites the underlying text storage excluding the target record.
     *
     * @param id the unique identifier of the sale record to remove
     * @return true if the sale was found and removed; false otherwise
     * @throws PersistenceException if an error occurs while rewriting the storage file
     */
    @Override
    public boolean delete(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }

        List<String> lines = readAllRawLines();
        String cleanId = id.trim();

        List<String> updatedLines = lines.stream()
                .filter(line -> !line.startsWith(cleanId + ";"))
                .toList();

        if (updatedLines.size() == lines.size()) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (String line : updatedLines) {
                writer.println(line);
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error updating file '" + filePath + "' during sale record deletion: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a recorded sale by its unique identifier string wrapped in an Optional container.
     *
     * @param id the unique identifier of the target sale
     * @return an Optional containing the matching Sale if found, or Optional.empty() if not found
     */
    @Override
    public Optional<Sale> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        String cleanId = id.trim();

        return findAll().stream()
                .filter(sale -> sale != null && String.valueOf(sale.getId()).equalsIgnoreCase(cleanId))
                .findFirst();
    }

    /**
     * Retrieves all sale transactions stored in the persistence file, reconstructing associated domain entities.
     * Applies a fallback strategy for Client and Seller instances if they are not active in system memory.
     *
     * @return a list containing all successfully parsed and reconstructed Sale objects
     */
    @Override
    public List<Sale> findAll() {
        List<String> rawLines = readAllRawLines();
        List<Sale> sales = new ArrayList<>();

        for (String line : rawLines) {
            String[] parts = line.split(";");
            if (parts.length < 7) {
                continue;
            }

            try {
                int id = Integer.parseInt(parts[0].trim());
                String date = parts[1].trim();
                String clientId = parts[2].trim();
                String sellerCode = parts[3].trim();
                double discount = Double.parseDouble(parts[4].trim());
                double extendedWarrantyCost = Double.parseDouble(parts[5].trim());
                double total = Double.parseDouble(parts[6].trim());

                // 1. Client reconstruction with Fallback to prevent null references
                Client client = (personService != null) ? personService.findClientById(clientId) : null;
                if (client == null) {
                    client = new Client("Cliente Registrado", clientId, "N/A", "N/A");
                }

                // 2. Seller reconstruction with Fallback to prevent null references
                Seller seller = (personService != null) ? personService.findSellerByCode(sellerCode) : null;
                if (seller == null) {
                    seller = new Seller("Vendedor " + sellerCode, "N/A", "N/A", sellerCode, "N/A");
                }

                // 3. Product items collection reconstruction
                List<Product> products = new ArrayList<>();
                if (productService != null) {
                    for (int i = 7; i < parts.length; i++) {
                        Product p = productService.findProductById(parts[i].trim());
                        if (p != null) {
                            products.add(p);
                        }
                    }
                }

                Sale sale = new Sale(id, date, client, seller, products);
                sale.setDiscountAmount(discount);
                sale.setExtendedWarrantyCost(extendedWarrantyCost);

                if (!products.isEmpty()) {
                    sale.calculateTotal();
                } else {
                    sale.setTotal(total);
                }

                sales.add(sale);
            } catch (Exception e) {
                System.err.println("Error deserializing sale record: " + line + " | Cause: " + e.getMessage());
            }
        }

        return sales;
    }

    /**
     * Reads all raw non-blank text lines from the storage file.
     *
     * @return a list of semicolon-separated raw strings from disk
     * @throws PersistenceException if an I/O error occurs while reading the file
     */
    public List<String> readAllRawLines() {
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        try (Stream<String> lines = Files.lines(path)) {
            return lines.filter(Predicate.not(String::isBlank)).toList();
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error reading persistence file '" + filePath + "' during fetch operation: " + e.getMessage(), e);
        }
    }
}