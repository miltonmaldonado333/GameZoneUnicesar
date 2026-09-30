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
 * Service class handling the business logic for managing promotions and
 * discounts.
 */
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void registerPercentageDiscount(String id, String name, LocalDate startDate,
            LocalDate endDate, double percentage) {
        List<Promotion> promotions = promotionRepository.loadAll();
        // model: (percentage, id, name, startDate, endDate)
        promotions.add(new PercentageDiscount(percentage, id, name, startDate, endDate));
        promotionRepository.saveAll(promotions);
    }

    public void registerCategoryDiscount(String id, String name, LocalDate startDate,LocalDate endDate, double percentage, String targetCategory) {
        if (!"VIDEOGAME".equalsIgnoreCase(targetCategory)&& !"CONSOLE".equalsIgnoreCase(targetCategory)&& !"ACCESSORY".equalsIgnoreCase(targetCategory)) {
            throw new IllegalArgumentException("La categoría objetivo debe ser VIDEOGAME, CONSOLE o ACCESSORY.");
        }

        List<Promotion> promotions = promotionRepository.loadAll();
        // model: (percentage, targetCategory, id, name, startDate, endDate)
        promotions.add(new CategoryDiscount(percentage, targetCategory.toUpperCase(), id, name, startDate, endDate));
        promotionRepository.saveAll(promotions);
    }

    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate,
            LocalDate endDate, int minQuantity, double percentage) {
        List<Promotion> promotions = promotionRepository.loadAll();
        // model: (minQuantity, percentage, id, name, startDate, endDate)
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

    public Promotion findById(String id) {
        for (Promotion promotion : promotionRepository.loadAll()) {
            if (promotion.getId().equals(id)) {
                return promotion;
            }
        }
        return null;
    }
}
