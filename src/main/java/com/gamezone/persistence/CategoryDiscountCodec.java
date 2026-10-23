package com.gamezone.persistence;

import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.Promotion;

/**
 * Codec of the category discount promotion type.
 */
public class CategoryDiscountCodec extends PromotionCodec<CategoryDiscount> {

    /**
     * Creates the codec of the category discount type.
     */
    public CategoryDiscountCodec() {
        super("CATEGORY", CategoryDiscount.class);
    }

    /**
     * Converts a category discount into its line.
     *
     * @param promotion the promotion to write
     * @return the text line representing the promotion
     */
    @Override
    protected String encode(CategoryDiscount promotion) {
        return line(promotion, String.valueOf(promotion.getPercentage()), promotion.getTargetCategory());
    }

    /**
     * Rebuilds a category discount from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt promotion
     */
    @Override
    public Promotion decode(String[] fields) {
        String id = fields[1];
        String name = fields[2];
        double percentage = Double.parseDouble(fields[5]);
        String targetCategory = fields[6];
        return new CategoryDiscount(percentage, targetCategory, id, name, startDate(fields), endDate(fields));
    }
}