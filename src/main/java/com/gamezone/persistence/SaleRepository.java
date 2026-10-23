package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Handles text file persistence operations for sales transactions. Maintains
 * low coupling by eliminating direct dependencies on the service layer.
 */
public class SaleRepository {

    private static final String FILE_NAME = "sales.txt";

    /**
     * Default constructor.
     */
    public SaleRepository() {
    }

    /**
     * Appends a new sale record to the text file including discount and total
     * values.
     *
     * @param sale the sale object to be saved
     * @throws PersistenceException if the sales file cannot be written
     */
    public void save(Sale sale) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            StringBuilder sb = new StringBuilder();

            sb.append(sale.getId()).append(";")
                    .append(sale.getDate()).append(";")
                    .append(sale.getClient().getIdentification()).append(";")
                    .append(sale.getSeller().getEmployeeCode()).append(";")
                    .append(sale.getDiscountAmount()).append(";")
                    .append(sale.getExtendedWarrantyCost()).append(";")
                    .append(sale.getTotal());

            for (Product p : sale.getProducts()) {
                sb.append(";").append(p.getId());
            }

            writer.println(sb.toString());
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error al escribir el archivo '" + FILE_NAME + "' (operacion: guardado de venta): " + e.getMessage(), e);
        }
    }

    /**
     * Reads all raw sale record lines from the text file.
     *
     * @return a list of raw semicolon-separated strings stored in the file
     * @throws PersistenceException if the sales file cannot be read
     */
    public List<String> readAllRawLines() {
        Path path = Path.of(FILE_NAME);

        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        try (Stream<String> lines = Files.lines(path)) {
            return lines.filter(Predicate.not(String::isBlank))
                    .toList();
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error al leer el archivo '" + FILE_NAME + "' (operacion: lectura de ventas): " + e.getMessage(), e);
        }
    }
}
