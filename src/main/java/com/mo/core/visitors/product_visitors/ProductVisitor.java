package com.mo.core.visitors.product_visitors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.*;

@Component
public interface ProductVisitor<T> {
    // Méthodes spécifiques par type de produit
    T visit(ServiceProduct product);
    T visit(RealEstateProduct product);
    T visit(VehicleProduct product);
    T visit(ElectronicProduct product);
    T visit(FoodProduct product);
    T visit(FashionProduct product);

    // Méthode générique de fallback
    @SuppressWarnings("unchecked")
    default T visit(AbstractProduct product) {
        // Trouve dynamiquement la méthode visit() correspondante
        try {
            Method visitMethod = this.getClass().getMethod("visit", product.getClass());
            return (T) visitMethod.invoke(this, product);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException(
                "No visitor implementation for " + product.getClass().getSimpleName(), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Visitor invocation failed", e);
        }
    }
}
