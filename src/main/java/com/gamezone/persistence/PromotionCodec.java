package com.gamezone.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamezone.model.Promotion;

/**
 * Base of the codecs of the promotion types. It writes the fields that every
 * promotion shares at the start of the line and each type adds its own fields
 * after them.
 *
 * @param <S> the concrete promotion type handled by the codec
 */
public abstract class PromotionCodec<S extends Promotion> extends SubtypeCodec<Promotion, S> {

    /**
     * Creates the codec of one promotion type.
     *
     * @param type    the discriminator written as the first field of the line
     * @param subtype the concrete promotion class this codec handles
     */
    protected PromotionCodec(String type, Class<S> subtype) {
        super(type, subtype);
    }

    /**
     * Builds the line of a promotion from its common fields and the fields of its type.
     *
     * @param promotion      the promotion to write
     * @param specificFields the fields that belong to the promotion type
     * @return the text line representing the promotion
     */
    protected String line(S promotion, String... specificFields) {
        List<String> parts = new ArrayList<>();
        parts.add(getType());
        parts.add(promotion.getId());
        parts.add(promotion.getName());
        parts.add(promotion.getStartDate().toString());
        parts.add(promotion.getEndDate().toString());
        parts.addAll(Arrays.asList(specificFields));
        return String.join(";", parts);
    }

    /**
     * Reads the start date from the fields of a line.
     *
     * @param fields the fields of the stored line
     * @return the start date
     */
    protected static LocalDate startDate(String[] fields) {
        return LocalDate.parse(fields[3]);
    }

    /**
     * Reads the end date from the fields of a line.
     *
     * @param fields the fields of the stored line
     * @return the end date
     */
    protected static LocalDate endDate(String[] fields) {
        return LocalDate.parse(fields[4]);
    }
}