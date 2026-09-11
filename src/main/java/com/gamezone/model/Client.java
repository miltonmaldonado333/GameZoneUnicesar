package com.gamezone.model;

/**
 * Represents a customer who purchases products at the store.
 * A client is a specialization of Person with an email
 * used to track their purchase history.
 */
public class Client extends Person {

    private String email;

    /**
     * Creates a new Client with the given information.
     *
     * @param name           the client's full name
     * @param identification the client's identification number
     * @param phone          the client's contact phone number
     * @param email          the client's email address
     */
    public Client(String name, String identification, String phone, String email) {
        super(name, identification, phone);
        this.email = email;
    }

    /**
     * Returns the client's email address.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the client's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
}