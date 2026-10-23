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

import com.gamezone.model.Promotion;

/**
 * File-based repository for persisting and retrieving {@link Promotion} instances.
 * <p>
 * Handles serialization and deserialization of promotion records to and from
 * a CSV file using a discriminator field to distinguish concrete promotion types.
 * The stored format of each type is defined by its own codec, registered in a
 * {@link CodecRegistry}, so a new promotion type is added with a new codec and
 * without changing this class.
 * </p>
 */
public class FilePromotionRepository implements PromotionRepository {

    /** Default file path for storing promotion data. */
    private static final String DEFAULT_FILE_PATH = "data/promotions.csv";

    /** Path to the target CSV persistence file. */
    private final String filePath;

    /** Codec of each promotion type. */
    private final CodecRegistry<Promotion> codecs;

    /**
     * Constructs a {@code PromotionRepository} using the default file path.
     */
    public FilePromotionRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a {@code PromotionRepository} with a custom file path and the default codecs.
     *
     * @param filePath the file path where promotion data is saved and loaded
     */
    public FilePromotionRepository(String filePath) {
        this(filePath, defaultCodecs());
    }

    /**
     * Constructs a {@code PromotionRepository} with a custom file path and the given codecs.
     *
     * @param filePath the file path where promotion data is saved and loaded
     * @param codecs   the registry with the codec of each promotion type
     */
    public FilePromotionRepository(String filePath, CodecRegistry<Promotion> codecs) {
        this.filePath = filePath;
        this.codecs = codecs;
    }

    private static CodecRegistry<Promotion> defaultCodecs() {
        return new CodecRegistry<Promotion>(true)
                .register(new PercentageDiscountCodec())
                .register(new CategoryDiscountCodec())
                .register(new BulkPurchaseDiscountCodec());
    }

    /**
     * Loads all persisted promotion records from the CSV file.
     *
     * @return a list of active and inactive {@link Promotion} objects, or an empty list if the file does not exist
     */
    @Override
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
    @Override
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
     * Serializes a {@link Promotion} instance into a formatted CSV record using
     * the codec of its type.
     *
     * @param promotion the promotion entity to format
     * @return a semi-colon delimited string representing the promotion record
     */
    private String toLine(Promotion promotion) {
        return codecs.encode(promotion);
    }

    /**
     * Deserializes a single line from the CSV file into the promotion of the type
     * named by its first field. Every line must carry valid dates, whatever its type.
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

            LocalDate.parse(fields[3]);
            LocalDate.parse(fields[4]);

            return codecs.decode(fields[0], fields);
        } catch (Exception e) {
            System.out.println("Error parsing line: " + line + " -> " + e.getMessage());
        }

        return null;
    }
}