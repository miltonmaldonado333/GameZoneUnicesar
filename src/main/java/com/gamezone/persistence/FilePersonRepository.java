package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Client;
import com.gamezone.model.Person;
import com.gamezone.model.Seller;

/**
<<<<<<< HEAD
 * Handles text file persistence operations for person records (clients and sellers).
 */
public class FilePersonRepository implements PersonRepository {

    // Target absolute file path for data storage
    private final String filePath;

    /**
     * Constructs a FilePersonRepository and ensures the file path is resolved
     * relative to the project's root directory.
     * 
     * @param filePath relative path of the file
     */
    public FilePersonRepository(String filePath) {
        // Ensures the file is created in the project root regardless of the IDE
        this.filePath = System.getProperty("user.dir") + File.separator + filePath;
    }

    /**
     * Loads all person records from the text file.
     * 
     * @return a list of parsed Person objects (Clients and Sellers)
=======
 * File-based implementation of PersonRepository.
 * Stores clients and sellers as plain text, one person per line,
 * using semicolons as field separators.
 */
public class FilePersonRepository implements PersonRepository {

    private String filePath;

    /**
     * Creates a repository that reads from and writes to the given file path.
     *
     * @param filePath the path of the file used to store person data
     */
    public FilePersonRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all persons stored in the file. If the file does not exist yet,
     * an empty list is returned instead of throwing an error.
     *
     * @return the list of persons loaded from the file
>>>>>>> 2e2633cfb5059fec128506d31e6140d857a984ef
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
            System.out.println("Error reading person data: " + e.getMessage());
        }

        return persons;
    }

    /**
<<<<<<< HEAD
     * Saves a complete list of person records to the text file.
     * 
     * @param persons the list of persons to be written
=======
     * Saves the given list of persons to the file, overwriting any previous content.
     *
     * @param persons the list of persons to save
>>>>>>> 2e2633cfb5059fec128506d31e6140d857a984ef
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
            System.out.println("Error writing person data: " + e.getMessage());
        }
    }

    /**
<<<<<<< HEAD
     * Converts a Person object into a semicolon-delimited string format.
     * 
     * @param person the person instance to format
     * @return string representation for text file storage
=======
     * Converts a single person into its text line representation.
     *
     * @param person the person to convert
     * @return the text line representing the person
>>>>>>> 2e2633cfb5059fec128506d31e6140d857a984ef
     */
    private String toLine(Person person) {
        if (person instanceof Client client) {
            return String.join(";",
                    "CLIENT",
                    client.getName(),
                    client.getIdentification(),
                    client.getPhone(),
                    client.getEmail());
        } else if (person instanceof Seller seller) {
            return String.join(";",
                    "SELLER",
                    seller.getName(),
                    seller.getIdentification(),
                    seller.getPhone(),
                    seller.getEmployeeCode(),
                    seller.getWorkShift());
        }
        return "";
    }

    /**
<<<<<<< HEAD
     * Parses a single text line into either a Client or Seller object.
     * 
     * @param line the text line from the file
     * @return the reconstructed Person object, or null if type is unrecognized
=======
     * Parses a single text line back into a Person object (Client or Seller).
     *
     * @param line the text line to parse
     * @return the resulting Person, or null if the line has an unknown format
>>>>>>> 2e2633cfb5059fec128506d31e6140d857a984ef
     */
    private Person parseLine(String line) {
        String[] fields = line.split(";");

        if (fields[0].equals("CLIENT")) {
            return new Client(fields[1], fields[2], fields[3], fields[4]);
        } else if (fields[0].equals("SELLER")) {
            return new Seller(fields[1], fields[2], fields[3], fields[4], fields[5]);
        }

        return null;
    }
}