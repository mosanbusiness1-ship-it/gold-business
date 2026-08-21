package com.mo.core.visitors;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Visitor {
    String value(); // Clé d'enregistrement (ex: "inventory")
}
