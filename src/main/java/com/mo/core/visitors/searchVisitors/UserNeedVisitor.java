package com.mo.core.visitors.searchVisitors;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.mo.core.documents.needs.*;

@Component
public interface UserNeedVisitor<R> {
    // Méthodes spécifiques par type de besoin
    R visit(ElectronicNeedDocument need);
    R visit(FashionNeedDocument need);
    R visit(FoodNeedDocument need);
    R visit(RealEstateNeedDocument need);
    R visit(ServiceNeedDocument need);
    R visit(VehicleNeedDocument need);

    // Méthode générique de fallback
    @SuppressWarnings("unchecked")
    default R visit(AbstractUserNeedDocument need) {
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



