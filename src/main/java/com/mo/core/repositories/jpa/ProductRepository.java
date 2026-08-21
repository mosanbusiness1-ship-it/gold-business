package com.mo.core.repositories.jpa;

import com.mo.core.model.products.AbstractProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends 
JpaRepository<AbstractProduct, Long>,
JpaSpecificationExecutor<AbstractProduct>,
ProductRepositoryCustom {
}
