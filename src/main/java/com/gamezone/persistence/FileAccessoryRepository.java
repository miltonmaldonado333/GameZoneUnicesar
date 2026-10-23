package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Accessory;

/**
 * File-based repository for Accessory objects (Controller, Cable, Memory).
 * Persists all accessories in a single CSV file using a type discriminator. The
 * stored format of each type is defined by its own codec, registered in a
 * CodecRegistry, so a new accessory type is added with a new codec and without
 * changing this class.
 */
public class FileAccessoryRepository implements AccessoryRepository {

    private static final String DEFAULT_FILE_PATH = "data/accessories.csv";

    private String filePath;
    private final CodecRegistry<Accessory> codecs;

    /**
     * Creates a repository that uses the default file path (data/accessories.csv).
     */
    public FileAccessoryRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a repository that reads from and writes to the given file path,
     * using the default codecs.
     *
     * @param filePath the path of the CSV file used to store accessory data
     */
    public FileAccessoryRepository(String filePath) {
        this(filePath, defaultCodecs());
    }

    /**
     * Creates a repository that reads from and writes to the given file path,
     * using the given codecs.
     *
     * @param filePath the path of the CSV file used to store accessory data
     * @param codecs   the registry with the codec of each accessory type
     */
    public FileAccessoryRepository(String filePath, CodecRegistry<Accessory> codecs) {
        this.filePath = filePath;
        this.codecs = codecs;
    }

    private static CodecRegistry<Accessory> defaultCodecs() {
        return new CodecRegistry<Accessory>(false)
                .register(new ControllerCodec())
                .register(new CableCodec())
                .register(new MemoryCodec());
    }

    /**
     * Loads all accessories stored in the file. If the file does not exist yet,
     * an empty list is returned instead of throwing an error.
     *
     * @return the list of accessories loaded from the file
     */
    @Override
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return accessories;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Accessory accessory = parseLine(line);
                if (accessory != null) {
                    accessories.add(accessory);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading accessory data: " + e.getMessage());
        }

        return accessories;
    }

    /**
     * Saves the given list of accessories to the file, overwriting any previous
     * content.
     *
     * @param accessories the list of accessories to save
     */
    @Override
    public void saveAll(List<Accessory> accessories) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        // Crear la carpeta 'data' (o la ruta especificada) si no existe
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Accessory accessory : accessories) {
                writer.write(toLine(accessory));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing accessory data: " + e.getMessage());
        }
    }

    /**
     * Converts a single accessory into its CSV line representation using the
     * codec of its type.
     *
     * @param accessory the accessory to convert
     * @return the text line representing the accessory
     */
    private String toLine(Accessory accessory) {
        return codecs.encode(accessory);
    }

    /**
     * Parses a single CSV line into the accessory of the type named by its first field.
     *
     * @param line the text line to parse
     * @return the resulting Accessory, or null if the line has an unknown
     * format
     */
    private Accessory parseLine(String line) {
        String[] fields = line.split(";", -1);
        return codecs.decode(fields[0], fields);
    }
}