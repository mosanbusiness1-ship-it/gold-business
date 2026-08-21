package com.mo.core.visitors.need_visitors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.mo.core.model.needs.*;


public interface UserNeedVisitor<R> {
    // Méthodes spécifiques par type de besoin
    R visit(ElectronicNeed need);
    R visit(FashionNeed need);
    R visit(FoodNeed need);
    R visit(RealEstateNeed need);
    R visit(ServiceNeed need);
    R visit(VehicleNeed need);

    // Méthode générique de fallback
    @SuppressWarnings("unchecked")
    default R visit(AbstractUserNeed need) {
        try {
            Method visitMethod = this.getClass().getMethod("visit", need.getClass());
            return (R) visitMethod.invoke(this, need);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException(
                "No visitor implementation for " + need.getClass().getSimpleName(), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Visitor invocation failed", e);
        }
    }
}


