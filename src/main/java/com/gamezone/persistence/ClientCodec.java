package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Person;

/**
 * Codec of the client role: writes and reads a client as a semicolon-delimited line.
 */
public class ClientCodec extends SubtypeCodec<Person, Client> {

    /**
     * Creates the codec of the client role.
     */
    public ClientCodec() {
        super("CLIENT", Client.class);
    }

    /**
     * Converts a client into its line.
     *
     * @param client the client to write
     * @return the text line representing the client
     */
    @Override
    protected String encode(Client client) {
        return String.join(";",
                getType(),
                client.getName(),
                client.getIdentification(),
                client.getPhone(),
                client.getEmail());
    }

    /**
     * Rebuilds a client from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt client
     */
    @Override
    public Person decode(String[] fields) {
        return new Client(fields[1], fields[2], fields[3], fields[4]);
    }
}