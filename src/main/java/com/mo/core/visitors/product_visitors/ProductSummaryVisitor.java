package com.mo.core.visitors.product_visitors;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;

@Component
public class ProductSummaryVisitor implements ProductVisitor<String> {

    @Override
    public String visit(FashionProduct product) {
        return String.format(
            "Vêtement de type %s de marque %s, taille %s et couleur %s.",
            product.getFashionType().name().toLowerCase(),
            product.getBrand(),
            product.getSize(),
            product.getColor()
        );
    }

    @Override
    public String visit(ElectronicProduct product) {
        return String.format(
            "Électronique : %s %s (%s), garantie %s.",
            product.getBrand(),
            product.getModel(),
            product.getElectronicType().name().toLowerCase(),
            product.getWarrantyPeriod()
        );
    }

    @Override
    public String visit(FoodProduct product) {
        return String.format(
            "Produit alimentaire : %s, %s, %s, expiration le %s.",
            product.getCategory().name().toLowerCase(),
            product.getOrganic() ? "bio" : "non bio",
            product.getGlutenFree() ? "sans gluten" : "avec gluten",
            product.getExpiryDate().toString()
        );
    }

    @Override
    public String visit(VehicleProduct product) {
        return String.format(
            "Véhicule %s %s (%s), %s, %.0f km, couleur %s.",
            product.getMake(),
            product.getModel(),
            product.getManufacturingYear(),
            product.getFuelType(),
            product.getMileage(),
            product.getColor()
        );
    }

    @Override
    public String visit(RealEstateProduct product) {
        return String.format(
            "Bien immobilier : %s situé à %s, %s m² à %.2f €.",
            product.getRealEstateType().name().toLowerCase(),
            product.getCity(),
            product.getSurfaceArea(),
            product.getPrice()
        );
    }

    @Override
    public String visit(ServiceProduct product) {
        String durationStr = "non spécifiée";
        if (product.getDuration() != null) {
            long minutes = product.getDuration() / 60; // conversion secondes → minutes
            durationStr = String.valueOf(minutes);
        }
        return String.format(
            "Service proposé par %s à %s, disponible en ligne : %s, durée : %s minutes.",
            product.getServiceProvider(),
            product.getLocation(),
            Boolean.TRUE.equals(product.getOnlineAvailable()) ? "oui" : "non",
            durationStr
        );
    }


    
}

