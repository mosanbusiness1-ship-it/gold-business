package com.mo.core.visitors.product_visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.mo.auth.User;
import com.mo.core.documents.products.VehicleProductDocument;
import com.mo.core.enums.ProductType;
import com.mo.core.enums.VehicleType;
import com.mo.core.model.products.VehicleProduct;

public class ProductIndexerVisitorTest {

    @Test
    public void vehicleProductIndexer_shouldPopulateNewVehicleFields() {
        VehicleProduct product = new VehicleProduct();
        product.setId(100L);
        product.setType(ProductType.VEHICLE);
        User owner = new User();
        owner.setId(1L);
        product.setOwner(owner);
        product.setName("Luxury SUV");
        product.setDescription("Comfortable and efficient");
        product.setPrice(BigDecimal.valueOf(42000));
        product.setCurrency(com.mo.core.enums.Currency.USD);
        product.setCreatedAt(LocalDateTime.now().minusDays(2));
        product.setUpdatedAt(LocalDateTime.now());
        product.setVehicleType(VehicleType.SUV);
        product.setMake("TestBrand");
        product.setModel("Model X");
        product.setTransmission("AUTO");
        product.setTrim("Premium");
        product.setFuelConsumptionLPer100km(8.5);
        product.setDoors(5);
        product.setVehicleCondition("USED");
        product.setWarrantyMonths(12);
        product.setSellerRating(4.8);

        ProductIndexerVisitor visitor = new ProductIndexerVisitor();
        ReflectionTestUtils.setField(visitor, "qualityEvaluationVisitor", new QualityEvaluationVisitor());

        VehicleProductDocument document = (VehicleProductDocument) visitor.visit(product);

        assertEquals("AUTO", document.getTransmission());
        assertEquals("Premium", document.getTrim());
        assertEquals(8.5, document.getFuelConsumptionLPer100km());
        assertEquals(5, document.getDoors());
        assertEquals("USED", document.getVehicleCondition());
        assertEquals(12, document.getWarrantyMonths());
        assertEquals(4.8, document.getSellerRating());
        assertTrue(document.getAllText().contains("AUTO"));
        assertTrue(document.getAllText().contains("Premium"));
    }
}
