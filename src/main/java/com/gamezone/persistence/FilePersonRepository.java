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
     * Saves a complete list of person records to the text file.
     * 
     * @param persons the list of persons to be written
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
     * Converts a Person object into a semicolon-delimited string format.
     * 
     * @param person the person instance to format
     * @return string representation for text file storage
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
     * Parses a single text line into either a Client or Seller object.
     * 
     * @param line the text line from the file
     * @return the reconstructed Person object, or null if type is unrecognized
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