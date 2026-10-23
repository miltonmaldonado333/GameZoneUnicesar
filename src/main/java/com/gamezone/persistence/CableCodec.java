package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;

/**
 * Codec of the cable accessory type.
 */
public class CableCodec extends AccessoryCodec<Cable> {

    /**
     * Creates the codec of the cable type.
     */
    public CableCodec() {
        super("CABLE", Cable.class);
    }

    /**
     * Converts a cable into its line.
     *
     * @param cable the cable to write
     * @return the text line representing the cable
     */
    @Override
    protected String encode(Cable cable) {
        return line(cable, String.valueOf(cable.getLength()), cable.getConnectorType());
    }

    /**
     * Rebuilds a cable from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt cable
     */
    @Override
    public Accessory decode(String[] fields) {
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stock = Integer.parseInt(fields[4]);
        double length = Double.parseDouble(fields[5]);
        String connectorType = fields[6];
        return new Cable(parseConsoleIds(fields[7]), id, title, price, stock, length, connectorType);
    }
}