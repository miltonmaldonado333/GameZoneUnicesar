package com.gamezone.service;

import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.Client;
import com.gamezone.model.Person;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

/**
 * Provides the business rules for managing clients and sellers.
 * Acts as the intermediary between the user interface and the
 * person persistence layer.
 */
public class PersonService {

    private PersonRepository personRepository;
    private List<Person> persons;

    /**
     * Creates a PersonService backed by the given repository.
     * Loads any previously stored persons immediately.
     *
     * @param personRepository the repository used to persist person data
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.persons = personRepository.loadPersons();
    }

    /**
     * Registers a new client and persists the updated list.
     *
     * @param name           the client's full name
     * @param identification the client's identification number
     * @param phone          the client's contact phone number
     * @param email          the client's email address
     */
    public void registerClient(String name, String identification, String phone, String email) {
        Client client = new Client(name, identification, phone, email);
        persons.add(client);
        personRepository.savePersons(persons);
    }

    /**
     * Registers a new seller and persists the updated list.
     * 
     * @param name           the seller's full name
     * @param identification the seller's identification number
     * @param phone          the seller's contact phone number
     * @param employeeCode   the seller's employee code
     * @param workShift      the seller's work shift
     */
    public void registerSeller(String name, String identification, String phone, String employeeCode, String workShift) {
        Seller seller = new Seller(name, identification, phone, employeeCode, workShift);
        persons.add(seller);
        personRepository.savePersons(persons);
    }

    /**
     * Returns all registered clients.
     *
     * @return a list containing only the clients
     */
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        for (Person person : persons) {
            if (person instanceof Client client) {
                clients.add(client);
            }
        }
        return clients;
    }

    /**
     * Returns all registered sellers.
     *
     * @return a list containing only the sellers
     */
    public List<Seller> getAllSellers() {
        List<Seller> sellers = new ArrayList<>();
        for (Person person : persons) {
            if (person instanceof Seller seller) {
                sellers.add(seller);
            }
        }
        return sellers;
    }

    /**
     * Finds a client by their identification number.
     *
     * @param id the identification number to search for
     * @return the matching client, or null if not found
     */
    public Client findClientById(String id) {
        for (Client client : getAllClients()) {
            if (client.getIdentification().equals(id)) {
                return client;
            }
        }
        return null;
    }

    /**
     * Finds a seller by their employee code.
     *
     * @param code the employee code to search for
     * @return the matching seller, or null if not found
     */
    public Seller findSellerByCode(String code) {
        for (Seller seller : getAllSellers()) {
            if (seller.getEmployeeCode().equals(code)) {
                return seller;
            }
        }
        return null;
    }
}