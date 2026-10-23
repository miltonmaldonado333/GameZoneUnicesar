package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Controller;

/**
 * Codec of the controller accessory type.
 */
public class ControllerCodec extends AccessoryCodec<Controller> {

    /**
     * Creates the codec of the controller type.
     */
    public ControllerCodec() {
        super("CONTROLLER", Controller.class);
    }

    /**
     * Converts a controller into its line.
     *
     * @param controller the controller to write
     * @return the text line representing the controller
     */
    @Override
    protected String encode(Controller controller) {
        return line(controller, controller.getConnectionType());
    }

    /**
     * Rebuilds a controller from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt controller
     */
    @Override
    public Accessory decode(String[] fields) {
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stock = Integer.parseInt(fields[4]);
        String connectionType = fields[5];
        return new Controller(parseConsoleIds(fields[6]), id, title, price, stock, connectionType);
    }
}