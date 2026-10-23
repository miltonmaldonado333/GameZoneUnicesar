package com.gamezone.persistence;

import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

/**
 * Codec of the percentage discount promotion type.
 */
public class PercentageDiscountCodec extends PromotionCodec<PercentageDiscount> {

    /**
     * Creates the codec of the percentage discount type.
     */
    public PercentageDiscountCodec() {
        super("PERCENTAGE", PercentageDiscount.class);
    }

    /**
     * Converts a percentage discount into its line.
     *
     * @param promotion the promotion to write
     * @return the text line representing the promotion
     */
    @Override
    protected String encode(PercentageDiscount promotion) {
        return line(promotion, String.valueOf(promotion.getPercentage()));
    }

    /**
     * Rebuilds a percentage discount from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt promotion
     */
    @Override
    public Promotion decode(String[] fields) {
        String id = fields[1];
        String name = fields[2];
        double percentage = Double.parseDouble(fields[5]);
        return new PercentageDiscount(percentage, id, name, startDate(fields), endDate(fields));
    }
}