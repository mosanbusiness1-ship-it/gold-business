package com.mo.core.visitors.product_visitors;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.AbstractProduct;
import com.mo.core.visitors.Visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductVisitorRegistry {

    private final Map<String, ProductVisitor<?>> visitors = new HashMap<>();

    public ProductVisitorRegistry(List<ProductVisitor<?>> visitorList) {
        for (ProductVisitor<?> visitor : visitorList) {
            Visitor annotation = visitor.getClass().getAnnotation(Visitor.class);
            if (annotation != null) {
                // On convertit la clé en majuscule pour correspondre à classSimpleName.toUpperCase()
                visitors.put(annotation.value().toUpperCase(), visitor);
            }
        }
    }

@SuppressWarnings("unchecked")
public <T> ProductVisitor<T> getVisitorTyped(String type) {
    ProductVisitor<?> visitor = visitors.get(type.toUpperCase());
    if (visitor == null) {
        String availableTypes = String.join(", ", visitors.keySet());
        throw new IllegalArgumentException("Unknown visitor type: " + type + ". Available types: " + availableTypes);
    }
    return (ProductVisitor<T>) visitor;
}

    public ProductVisitor<AbstractProduct> getVisitorForProductType(String classSimpleName) {
        String key = classSimpleName.toUpperCase();
        ProductVisitor<?> visitor = visitors.get(key);
        if (visitor == null) {
            String availableTypes = String.join(", ", visitors.keySet());
            throw new IllegalArgumentException("No visitor found for product type: " + classSimpleName + ". Available types: " + availableTypes);
        }
        @SuppressWarnings("unchecked")
        ProductVisitor<AbstractProduct> typedVisitor = (ProductVisitor<AbstractProduct>) visitor;
        return typedVisitor;
    }
}