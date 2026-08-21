package com.mo.core.visitors.product_visitors;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.AbstractProduct;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.repositories.jpa.ProductRepository;
import com.mo.core.validators.BusinessValidationRegistry;
import com.mo.core.validators.ProductValidator;
import com.mo.core.visitors.Visitor;

@Component
@Visitor("createProductVisitor")
public class CreateProductVisitor implements ProductVisitor<AbstractProduct> {

    private final ProductRepository repository;
    private final ProductValidator beanValidator;
    private final BusinessValidationRegistry businessValidator;

    public CreateProductVisitor(ProductRepository repository,
                                ProductValidator beanValidator,
                                BusinessValidationRegistry businessValidator) {
        this.repository = repository;
        this.beanValidator = beanValidator;
        this.businessValidator = businessValidator;
    }

    @Override
    public AbstractProduct visit(ServiceProduct product) {
        beanValidator.validate(product);
        businessValidator.validate(product);
        return repository.save(product);
    }

    @Override
    public AbstractProduct visit(RealEstateProduct product) {
        beanValidator.validate(product);
        businessValidator.validate(product);
        return repository.save(product);
    }

    @Override
    public AbstractProduct visit(VehicleProduct product) {
        beanValidator.validate(product);
        businessValidator.validate(product);
        return repository.save(product);
    }

    @Override
    public AbstractProduct visit(ElectronicProduct product) {
        beanValidator.validate(product);
        businessValidator.validate(product);
        return repository.save(product);
    }

    @Override
    public AbstractProduct visit(FoodProduct product) {
        beanValidator.validate(product);
        businessValidator.validate(product);
        return repository.save(product);
    }

    @Override
    public AbstractProduct visit(FashionProduct product) {
        beanValidator.validate(product);
        businessValidator.validate(product);
        return repository.save(product);
    }
}
