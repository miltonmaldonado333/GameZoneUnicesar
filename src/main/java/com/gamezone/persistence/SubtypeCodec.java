package com.gamezone.persistence;

/**
 * Describes how one concrete subtype is written to, and read from, a line of a
 * text file. Each subtype of a repository has its own codec, so adding a subtype
 * means adding a codec and registering it, without changing the repository.
 *
 * @param <B> the base type handled by the repository
 * @param <S> the concrete subtype handled by this codec
 */
public abstract class SubtypeCodec<B, S extends B> {

    private final String type;
    private final Class<S> subtype;

    /**
     * Creates a codec for one subtype.
     *
     * @param type    the discriminator written as the first field of the line
     * @param subtype the concrete class this codec handles
     */
    protected SubtypeCodec(String type, Class<S> subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    /**
     * Gets the discriminator written as the first field of the line.
     *
     * @return the discriminator
     */
    public String getType() {
        return type;
    }

    /**
     * Tells whether this codec handles the given item.
     *
     * @param item the item to write
     * @return true if the item is an instance of the subtype of this codec
     */
    public boolean supports(B item) {
        return subtype.isInstance(item);
    }

    /**
     * Converts an item that this codec supports into its line.
     *
     * @param item the item to write
     * @return the text line representing the item
     */
    public String encodeItem(B item) {
        return encode(subtype.cast(item));
    }

    /**
     * Converts an item of this subtype into its line.
     *
     * @param item the item to write
     * @return the text line representing the item
     */
    protected abstract String encode(S item);

    /**
     * Rebuilds an item of this subtype from the fields of a line.
     *
     * @param fields the fields of the stored line, the first one being the discriminator
     * @return the rebuilt item
     */
    public abstract B decode(String[] fields);
}