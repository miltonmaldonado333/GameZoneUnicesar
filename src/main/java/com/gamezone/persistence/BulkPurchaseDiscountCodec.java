package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.Promotion;

/**
 * Codec of the bulk purchase discount promotion type.
 */
public class BulkPurchaseDiscountCodec extends PromotionCodec<BulkPurchaseDiscount> {

    /**
     * Creates the codec of the bulk purchase discount type.
     */
    public BulkPurchaseDiscountCodec() {
        super("BULK", BulkPurchaseDiscount.class);
    }

    /**
     * Converts a bulk purchase discount into its line.
     *
     * @param promotion the promotion to write
     * @return the text line representing the promotion
     */
    @Override
    protected String encode(BulkPurchaseDiscount promotion) {
        return line(promotion, String.valueOf(promotion.getMinQuantity()), String.valueOf(promotion.getPercentage()));
    }

    /**
     * Rebuilds a bulk purchase discount from the fields of its line.
     *
     * @param fields the fields of the stored line
     * @return the rebuilt promotion
     */
    @Override
    public Promotion decode(String[] fields) {
        String id = fields[1];
        String name = fields[2];
        int minQuantity = Integer.parseInt(fields[5]);
        double percentage = Double.parseDouble(fields[6]);
        return new BulkPurchaseDiscount(minQuantity, percentage, id, name, startDate(fields), endDate(fields));
    }
}