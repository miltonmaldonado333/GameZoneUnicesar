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
     */
    @Override
    public List<Person> loadPersons() {
        List<Person> persons = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return persons;
        }

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
     * Saves the given list of persons to the file, overwriting any previous content.
     *
     * @param persons the list of persons to save
     */
    @Override
    public void savePersons(List<Person> persons) {
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
     * Converts a single person into its text line representation.
     *
     * @param person the person to convert
     * @return the text line representing the person
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
     * Parses a single text line back into a Person object (Client or Seller).
     *
     * @param line the text line to parse
     * @return the resulting Person, or null if the line has an unknown format
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