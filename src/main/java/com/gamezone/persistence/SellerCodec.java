package com.gamezone.persistence;

import com.gamezone.model.Person;
import com.gamezone.model.Seller;

/**
 * Codec of the seller role: writes and reads a seller as a semicolon-delimited line.
 */
public class SellerCodec extends SubtypeCodec<Person, Seller> {

    /**
     * Creates the codec of the seller role.
     */
    public SellerCodec() {
        super("SELLER", Seller.class);
    }

    /**
     * Converts a seller into its line.
     *
     * @param seller the seller to write
     * @return the text line representing the seller
     */
    @Override
    protected String encode(Seller seller) {
        return String.join(";",
                getType(),
                seller.getName(),
                seller.getIdentification(),
                seller.getPhone(),
                seller.getEmployeeCode(),
                seller.getWorkShift());
    }

    /**
     * Rebuilds a seller from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt seller
     */
    @Override
    public Person decode(String[] fields) {
        return new Seller(fields[1], fields[2], fields[3], fields[4], fields[5]);
    }
}