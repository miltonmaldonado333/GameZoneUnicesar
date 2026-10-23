package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Person;

/**
 * Handles text file persistence operations for person records (clients and sellers).
 * Any IOException raised while reading or writing the file is wrapped in a
 * PersistenceException that names the file and the failed operation. The stored
 * format of each role is defined by its own codec, registered in a CodecRegistry,
 * so a new role is added with a new codec and without changing this class.
 */
public class FilePersonRepository implements PersonRepository {

    // Target absolute file path for data storage
    private final String filePath;
    private final CodecRegistry<Person> codecs;

    /**
     * Constructs a FilePersonRepository with the default codecs and ensures the
     * file path is resolved relative to the project's root directory.
     *
     * @param filePath relative path of the file
     */
    public FilePersonRepository(String filePath) {
        this(filePath, defaultCodecs());
    }

    /**
     * Constructs a FilePersonRepository with the given codecs and ensures the
     * file path is resolved relative to the project's root directory.
     *
     * @param filePath relative path of the file
     * @param codecs   the registry with the codec of each person role
     */
    public FilePersonRepository(String filePath, CodecRegistry<Person> codecs) {
        // Ensures the file is created in the project root regardless of the IDE
        this.filePath = System.getProperty("user.dir") + File.separator + filePath;
        this.codecs = codecs;
    }

    private static CodecRegistry<Person> defaultCodecs() {
        return new CodecRegistry<Person>(false)
                .register(new ClientCodec())
                .register(new SellerCodec());
    }

    /**
     * Loads all person records from the text file.
     *
     * @return a list of parsed Person objects (Clients and Sellers)
     * @throws PersistenceException if the file cannot be read
     */
    @Override
    public List<Person> loadPersons() {
        List<Person> persons = new ArrayList<>();
        File file = new File(filePath);

        // Return empty list if the persistence file does not exist yet
        if (!file.exists()) {
            return persons;
        }

        // Read records line by line using a buffered reader
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Person person = parseLine(line);
                if (person != null) {
                    persons.add(person);
                }
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error al leer el archivo '" + file.getName() + "' (operacion: lectura de personas): " + e.getMessage(), e);
        }

        return persons;
    }

    /**
     * Saves a complete list of person records to the text file.
     *
     * @param persons the list of persons to be written
     * @throws PersistenceException if the file cannot be written
     */
    @Override
    public void savePersons(List<Person> persons) {
        // Open file writer to overwrite records with current list state
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Person person : persons) {
                writer.write(toLine(person));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error al escribir el archivo '" + new File(filePath).getName() + "' (operacion: guardado de personas): " + e.getMessage(), e);
        }
    }

    /**
     * Converts a Person object into a semicolon-delimited string format using
     * the codec of its role.
     *
     * @param person the person instance to format
     * @return string representation for text file storage
     */
    private String toLine(Person person) {
        return codecs.encode(person);
    }

    /**
     * Parses a single text line into the person of the role named by its first field.
     *
     * @param line the text line from the file
     * @return the reconstructed Person object, or null if type is unrecognized
     */
    private Person parseLine(String line) {
        String[] fields = line.split(";");
        return codecs.decode(fields[0], fields);
    }
}