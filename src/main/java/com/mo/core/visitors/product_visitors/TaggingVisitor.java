package com.mo.core.visitors.product_visitors;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;

@Component
public class TaggingVisitor implements ProductVisitor<List<String>> {

    @Override
    public List<String> visit(FashionProduct product) {
        return List.of(
            "fashion",
            product.getBrand().toLowerCase(),
            product.getFashionType().name().toLowerCase(),
            product.getColor().toLowerCase(),
            product.getSize().toLowerCase()
        );
    }

    @Override
    public List<String> visit(ElectronicProduct product) {
        return List.of(
            "electronic",
            product.getBrand().toLowerCase(),
            product.getModel().toLowerCase(),
            product.getElectronicType().name().toLowerCase(),
            product.getWarrantyPeriod().toLowerCase()
        );
    }

    @Override
    public List<String> visit(FoodProduct product) {
    List<String> tags = new ArrayList<>();
    tags.add("food");

    if (product.getCategory() != null) {
        tags.add(product.getCategory().name().toLowerCase());
    }

    if (Boolean.TRUE.equals(product.getOrganic())) {
        tags.add("organic");
    }

    if (Boolean.TRUE.equals(product.getGlutenFree())) {
        tags.add("gluten-free");
    }

    if (product.getExpiryDate() != null && 
        product.getExpiryDate().isBefore(LocalDate.now().plusDays(7))) {
        tags.add("expiring-soon");
    }

    return tags;
}


    @Override
    public List<String> visit(VehicleProduct product) {
        List<String> tags = new ArrayList<>();
        tags.add("vehicle");
        tags.add(product.getVehicleType().name().toLowerCase());
        tags.add(product.getMake().toLowerCase());
        tags.add(product.getModel().toLowerCase());
        tags.add(product.getColor().toLowerCase());
        tags.add(product.getFuelType().toLowerCase());
        if (product.getMileage() != null && product.getMileage() < 50000) {
            tags.add("low-mileage");
        }
        return tags;
    }

    @Override
    public List<String> visit(RealEstateProduct product) {
        return List.of(
            "real-estate",
            product.getRealEstateType().name().toLowerCase(),
            product.getAddress().toLowerCase()
        );
    }

    @Override
    public List<String> visit(ServiceProduct product) {
        List<String> tags = new ArrayList<>();
        tags.add("service");
        tags.add(product.getServiceProvider().toLowerCase());
        tags.add(product.getLocation().toLowerCase());
        if (Boolean.TRUE.equals(product.getOnlineAvailable())) tags.add("online");
        return tags;
    }

    
}

