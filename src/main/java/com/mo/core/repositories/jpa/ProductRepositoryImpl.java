package com.mo.core.repositories.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import com.mo.core.enums.ProductType;
import com.mo.core.model.products.AbstractProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<AbstractProduct> searchWithFilters(String name, ProductType type, BigDecimal minPrice, BigDecimal maxPrice) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AbstractProduct> query = cb.createQuery(AbstractProduct.class);
        Root<AbstractProduct> root = query.from(AbstractProduct.class);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (type != null) {
            predicates.add(cb.equal(root.get("type"), type));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
    }
}

