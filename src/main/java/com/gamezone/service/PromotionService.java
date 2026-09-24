package com.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.persistence.PromotionRepository;

/**
 * Handles the business logic for managing promotions.
 */
public class PromotionService {

    private PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void registerPercentageDiscount(String id, String name, LocalDate startDate,
                                            LocalDate endDate, double percentage) {
        List<Promotion> promotions = promotionRepository.loadAll();
        promotions.add(new PercentageDiscount(percentage, id, name, startDate, endDate));
        promotionRepository.saveAll(promotions);
    }

    public void registerCategoryDiscount(String id, String name, LocalDate startDate,
                                          LocalDate endDate, double percentage, String targetCategory) {
        List<Promotion> promotions = promotionRepository.loadAll();
        promotions.add(new CategoryDiscount(percentage, targetCategory, id, name, startDate, endDate));
        promotionRepository.saveAll(promotions);
    }

    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate,
                                              LocalDate endDate, int minQuantity, double percentage) {
        List<Promotion> promotions = promotionRepository.loadAll();
        promotions.add(new BulkPurchaseDiscount(minQuantity, percentage, id, name, startDate, endDate));
        promotionRepository.saveAll(promotions);
    }

    public List<Promotion> listAllPromotions() {
        return promotionRepository.loadAll();
    }

    public List<Promotion> listActivePromotions() {
        List<Promotion> active = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Promotion promotion : promotionRepository.loadAll()) {
            if (promotion.isActive(today)) {
                active.add(promotion);
            }
        }
        return active;
    }

    public Promotion findBestPromotionFor(Sale sale) {
        Promotion bestPromotion = null;
        double bestDiscount = 0.0;

        for (Promotion promotion : listActivePromotions()) {
            double discount = promotion.calculateDiscount(sale);
            if (discount > bestDiscount) {
                bestDiscount = discount;
                bestPromotion = promotion;
            }
        }

        return bestPromotion;
    }
}


