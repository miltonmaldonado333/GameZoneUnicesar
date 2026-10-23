package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Memory;

/**
 * Codec of the memory accessory type.
 */
public class MemoryCodec extends AccessoryCodec<Memory> {

    /**
     * Creates the codec of the memory type.
     */
    public MemoryCodec() {
        super("MEMORY", Memory.class);
    }

    /**
     * Converts a memory into its line.
     *
     * @param memory the memory to write
     * @return the text line representing the memory
     */
    @Override
    protected String encode(Memory memory) {
        return line(memory, String.valueOf(memory.getCapacityGB()), memory.getStorageType());
    }

    /**
     * Rebuilds a memory from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt memory
     */
    @Override
    public Accessory decode(String[] fields) {
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stock = Integer.parseInt(fields[4]);
        int capacity = Integer.parseInt(fields[5]);
        String storageType = fields[6];
        return new Memory(capacity, storageType, id, title, price, stock, parseConsoleIds(fields[7]));
    }
}