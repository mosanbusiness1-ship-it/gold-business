package com.mo.core.visitors.product_visitors;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.mo.core.model.products.FoodProduct;
import com.mo.auth.User;

public class ProductRankingTest {

    private final QualityEvaluationVisitor visitor = new QualityEvaluationVisitor();

    @Test
    public void testFoodProductQualityRanking_HighQualityVsLowQuality() {
        // Product A: High quality
        FoodProduct highQuality = new FoodProduct();
        highQuality.setExpiryDate(LocalDate.now().plusDays(60)); // Well within range
        highQuality.setOrganic(Boolean.TRUE);
        highQuality.setGlutenFree(Boolean.TRUE);
        highQuality.setWeight(2.0);
        highQuality.setNutritionalInfo("{calories:150, protein:10}");
        highQuality.setName("Premium Organic Product");
        User owner = new User();
        owner.setId(1L);
        highQuality.setOwner(owner);

        // Product B: Low quality
        FoodProduct lowQuality = new FoodProduct();
        lowQuality.setExpiryDate(LocalDate.now().plusDays(3)); // Near expiry
        lowQuality.setOrganic(Boolean.FALSE);
        lowQuality.setGlutenFree(Boolean.FALSE);
        lowQuality.setWeight(null);
        lowQuality.setNutritionalInfo(null);
        lowQuality.setName("Basic Product");
        lowQuality.setOwner(owner);

        double scoreHigh = visitor.visit(highQuality);
        double scoreLow = visitor.visit(lowQuality);

        // High quality product should have significantly higher score
        assertTrue(scoreHigh > scoreLow, 
            "High quality product score (" + scoreHigh + ") should be > low quality score (" + scoreLow + ")");
        
        // High quality score should be > 0.5 (well-filled product)
        assertTrue(scoreHigh > 0.5, "High quality product should score > 0.5, got " + scoreHigh);
        
        // Low quality product should be relatively low
        assertTrue(scoreLow < 0.5, "Low quality product should score < 0.5, got " + scoreLow);
    }
}
