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

public class FilePersonRepository implements PersonRepository {

    private final String filePath;

    public FilePersonRepository(String filePath) {
        // Asegura que el archivo se cree en la raíz del proyecto sin importar el IDE
        this.filePath = System.getProperty("user.dir") + File.separator + filePath;
    }

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
                    seller.getEmployeeCode(),
                    seller.getWorkShift());
        }
        return "";
    }

    private Person parseLine(String line) {
        String[] fields = line.split(";");

        if (fields[0].equals("CLIENT")) {
            return new Client(fields[1], fields[2], fields[3], fields[4]);
        } else if (fields[0].equals("SELLER")) {
            return new Seller(fields[1], fields[2], fields[3]);
        }

        return null;
    }
}