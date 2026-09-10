package com.gamezone.model;

/**
 * Represents a generic person interacting with the store.
 * This class is abstract because a person must always be
 * a specific role (Client or Seller); it cannot be instantiated
 * on its own.
 */
public abstract class Person {

    private String name;
    private String identification;
    private String phone;

    /**
     * Creates a new Person with the given basic information.
     *
     * @param name           the person's full name
     * @param identification the person's identification number
     * @param phone          the person's contact phone number
     */
    public Person(String name, String identification, String phone) {
        this.name = name;
        this.identification = identification;
        this.phone = phone;
    }

    /**
     * Returns the person's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the person's name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the person's identification number.
     *
     * @return the identification
     */
    public String getIdentification() {
        return identification;
    }

    /**
     * Sets the person's identification number.
     *
     * @param identification the new identification
     */
    public void setIdentification(String identification) {
        this.identification = identification;
    }

    /**
     * Returns the person's contact phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the person's contact phone number.
     *
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}