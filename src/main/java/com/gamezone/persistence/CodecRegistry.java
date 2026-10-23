package com.gamezone.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the codecs of the subtypes of one repository and uses them to write and
 * to read lines. Writing picks the first codec that supports the item and reading
 * picks the codec whose discriminator matches the stored one, so the repository
 * has no chain of type tests.
 *
 * @param <B> the base type handled by the repository
 */
public class CodecRegistry<B> {

    private final List<SubtypeCodec<B, ? extends B>> codecs = new ArrayList<>();
    private final boolean ignoreCase;

    /**
     * Creates an empty registry.
     *
     * @param ignoreCase true if the stored discriminator is matched ignoring case
     */
    public CodecRegistry(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    /**
     * Registers the codec of a subtype.
     *
     * @param codec the codec to register
     * @return this registry, to chain registrations
     */
    public CodecRegistry<B> register(SubtypeCodec<B, ? extends B> codec) {
        codecs.add(codec);
        return this;
    }

    /**
     * Converts an item into its line using the first codec that supports it.
     *
     * @param item the item to write
     * @return the line, or an empty text if no codec supports the item
     */
    public String encode(B item) {
        for (SubtypeCodec<B, ? extends B> codec : codecs) {
            if (codec.supports(item)) {
                return codec.encodeItem(item);
            }
        }
        return "";
    }

    /**
     * Rebuilds an item using the codec whose discriminator matches the stored one.
     *
     * @param type   the discriminator read from the line
     * @param fields the fields of the stored line
     * @return the rebuilt item, or null if no codec has that discriminator
     */
    public B decode(String type, String[] fields) {
        for (SubtypeCodec<B, ? extends B> codec : codecs) {
            boolean sameType = ignoreCase ? codec.getType().equalsIgnoreCase(type) : codec.getType().equals(type);
            if (sameType) {
                return codec.decode(fields);
            }
        }
        return null;
    }
}