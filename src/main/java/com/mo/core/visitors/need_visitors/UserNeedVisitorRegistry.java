package com.mo.core.visitors.need_visitors;

import org.springframework.stereotype.Component;

import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.visitors.Visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserNeedVisitorRegistry {

    private final Map<String, UserNeedVisitor<?>> visitors = new HashMap<>();

    public UserNeedVisitorRegistry(List<UserNeedVisitor<?>> visitorList) {
        for (UserNeedVisitor<?> visitor : visitorList) {
            Visitor annotation = visitor.getClass().getAnnotation(Visitor.class);
            if (annotation != null) {
                // On convertit la clé en majuscule pour correspondre à classSimpleName.toUpperCase()
                visitors.put(annotation.value().toUpperCase(), visitor);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> UserNeedVisitor<T> getVisitorTyped(String type) {
        UserNeedVisitor<?> visitor = visitors.get(type.toUpperCase());
        if (visitor == null) {
            String availableTypes = String.join(", ", visitors.keySet());
            throw new IllegalArgumentException("Unknown visitor type: " + type + ". Available types: " + availableTypes);
        }
        return (UserNeedVisitor<T>) visitor;
    }

    public UserNeedVisitor<AbstractUserNeed> getVisitorForNeedType(String classSimpleName) {
        String key = classSimpleName.toUpperCase();
        UserNeedVisitor<?> visitor = visitors.get(key);
        if (visitor == null) {
            String availableTypes = String.join(", ", visitors.keySet());
            throw new IllegalArgumentException("No visitor found for need type: " + classSimpleName + ". Available types: " + availableTypes);
        }
        @SuppressWarnings("unchecked")
        UserNeedVisitor<AbstractUserNeed> typedVisitor = (UserNeedVisitor<AbstractUserNeed>) visitor;
        return typedVisitor;
    }
}

