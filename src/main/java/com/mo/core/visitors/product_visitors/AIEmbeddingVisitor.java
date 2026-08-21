package com.mo.core.visitors.product_visitors;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;

@Component
public class AIEmbeddingVisitor implements ProductVisitor<List<Object>> {

    @Override
    public List<Object> visit(FashionProduct product) {
        return List.of(
            "fashion",
            product.getBrand(),
            product.getFashionType() != null ? product.getFashionType().name() : null,
            product.getColor(),
            product.getSize()
        );
    }

    @Override
    public List<Object> visit(ElectronicProduct product) {
        return List.of(
            "electronic",
            product.getBrand(),
            product.getModel(),
            product.getElectronicType() != null ? product.getElectronicType().name() : null,
            product.getWarrantyPeriod()
        );
    }

    @Override
    public List<Object> visit(FoodProduct product) {
        return List.of(
            "food",
            product.getCategory() != null ? product.getCategory().name() : null,
            product.getExpiryDate() != null ? product.getExpiryDate().toString() : null,
            product.getOrganic() != null ? product.getOrganic() : false,
            product.getGlutenFree() != null ? product.getGlutenFree() : false,
            product.getWeight()
        );
    }

    @Override
    public List<Object> visit(VehicleProduct product) {
        return List.of(
            "vehicle",
            product.getVehicleType() != null ? product.getVehicleType().name() : null,
            product.getMake(),
            product.getModel(),
            product.getManufacturingYear(),
            product.getMileage(),
            product.getFuelType(),
            product.getColor()
        );
    }

    @Override
    public List<Object> visit(RealEstateProduct product) {
        return List.of(
            "real_estate",
            product.getRealEstateType() != null ? product.getRealEstateType().name() : null,
            product.getCity(),
            product.getSurfaceArea(),
            product.getPrice()
        );
    }

    @Override
    public List<Object> visit(ServiceProduct product) {
        return List.of(
            "service",
            product.getServiceProvider(),
            product.getLocation(),
            product.getDuration() != null ? product.getDuration() : null,
            product.getAvailableSlots() != null ? product.getAvailableSlots().size() : 0,
            product.getOnlineAvailable() != null ? product.getOnlineAvailable() : false
        );
    }
}
