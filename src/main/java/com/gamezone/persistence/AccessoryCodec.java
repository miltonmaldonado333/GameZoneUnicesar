package com.gamezone.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamezone.model.Accessory;

/**
 * Base of the codecs of the accessory types. It writes the fields that every
 * accessory shares at the start of the line and the list of compatible consoles
 * at the end, and each type adds its own fields in between.
 *
 * @param <S> the concrete accessory type handled by the codec
 */
public abstract class AccessoryCodec<S extends Accessory> extends SubtypeCodec<Accessory, S> {

    /**
     * Creates the codec of one accessory type.
     *
     * @param type    the discriminator written as the first field of the line
     * @param subtype the concrete accessory class this codec handles
     */
    protected AccessoryCodec(String type, Class<S> subtype) {
        super(type, subtype);
    }

    /**
     * Builds the line of an accessory from its common fields, the fields of its
     * type and the list of compatible consoles.
     *
     * @param accessory      the accessory to write
     * @param specificFields the fields that belong to the accessory type
     * @return the text line representing the accessory
     */
    protected String line(S accessory, String... specificFields) {
        List<String> parts = new ArrayList<>();
        parts.add(getType());
        parts.add(accessory.getId());
        parts.add(accessory.getTitle());
        parts.add(String.valueOf(accessory.getPrice()));
        parts.add(String.valueOf(accessory.getStock()));
        parts.addAll(Arrays.asList(specificFields));
        parts.add(String.join(",", accessory.getCompatibleConsoleIds()));
        return String.join(";", parts);
    }

    /**
     * Parses the comma-separated console IDs field into a list.
     *
     * @param field the raw field containing zero or more console IDs
     * @return a list of console IDs, empty if the field is blank
     */
    protected static List<String> parseConsoleIds(String field) {
        if (field == null || field.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(field.split(",")));
    }
}