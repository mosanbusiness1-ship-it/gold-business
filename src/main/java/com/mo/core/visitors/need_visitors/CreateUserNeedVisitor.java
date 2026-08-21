package com.mo.core.visitors.need_visitors;

import org.springframework.stereotype.Component;

import com.mo.core.model.needs.*;
import com.mo.core.repositories.jpa.UserNeedRepository;
import com.mo.core.visitors.Visitor;

@Component
@Visitor("createUserNeedVisitor")
public class CreateUserNeedVisitor implements UserNeedVisitor<AbstractUserNeed> {

    private final UserNeedRepository repository;

    public CreateUserNeedVisitor(UserNeedRepository repository) {
        this.repository = repository;
    }

    @Override
    public AbstractUserNeed visit(VehicleNeed need) {
        return repository.save(need);
    }

    @Override
    public AbstractUserNeed visit(ElectronicNeed need) {
        return repository.save(need);
    }

    @Override
    public AbstractUserNeed visit(FashionNeed need) {
        return repository.save(need);
    }

    @Override
    public AbstractUserNeed visit(FoodNeed need) {
        return repository.save(need);
    }

    @Override
    public AbstractUserNeed visit(RealEstateNeed need) {
        return repository.save(need);
    }

    @Override
    public AbstractUserNeed visit(ServiceNeed need) {
        return repository.save(need);
    }
}

