package com.mo.core.repositories.jpa;

import com.mo.core.enums.RealEstateType;
import com.mo.core.model.products.RealEstateProduct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstateProduct, Long> {
    List<RealEstateProduct> findByRealEstateType(RealEstateType type);
    List<RealEstateProduct> findBySurfaceAreaBetween(Double min, Double max);
    List<RealEstateProduct> findByIsForRentTrue();
    List<RealEstateProduct> findByIsForSaleTrue();
}