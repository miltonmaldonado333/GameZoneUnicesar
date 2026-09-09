package com.gamezone.persistence;

import java.util.List;

import com.gamezone.model.Person;

/**
 * Defines the persistence operations available for Person objects.
 * Implementations are responsible for choosing how and where
 * the data is actually stored.
 */
public interface PersonRepository {

    /**
     * Loads all persons (clients and sellers) previously stored.
     *
     * @return a list of persons; empty if no data has been stored yet
     */
    List<Person> loadPersons();

    /**
     * Saves the given list of persons, replacing any previously stored data.
     *
     * @param persons the list of persons to save
     */
    void savePersons(List<Person> persons);
}