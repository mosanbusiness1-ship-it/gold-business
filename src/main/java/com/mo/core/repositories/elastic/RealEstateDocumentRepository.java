package com.mo.core.repositories.elastic;

import com.mo.core.documents.products.RealEstateProductDocument;
import com.mo.core.enums.RealEstateType;
import com.mo.core.model.products.RealEstateProduct;

import java.util.List;

public interface RealEstateDocumentRepository {
    List<RealEstateProduct> findByRealEstateType(RealEstateType type);
    List<RealEstateProduct> findBySurfaceAreaBetween(Double min, Double max);
    List<RealEstateProduct> findByIsForRentTrue();
    List<RealEstateProduct> findByIsForSaleTrue();
}