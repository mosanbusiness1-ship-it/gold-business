package com.mo.core.visitors.product_visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.mo.core.model.products.FoodProduct;

public class QualityEvaluationVisitorTest {

    @Test
    public void foodProductQualityScore_expectedHigh() {
        FoodProduct p = new FoodProduct();
        p.setExpiryDate(LocalDate.now().plusDays(31));
        p.setOrganic(Boolean.TRUE);
        p.setGlutenFree(Boolean.FALSE);
        p.setWeight(1.5);
        p.setNutritionalInfo("{calories:100}");

        QualityEvaluationVisitor visitor = new QualityEvaluationVisitor();
        Double score = visitor.visit(p);

        // Expected contributions: expiry 0.4 + organic 0.2 + weight 0.1 + nutritional 0.2 = 0.9
        assertEquals(0.9, Math.round(score * 10.0) / 10.0);
    }
}
