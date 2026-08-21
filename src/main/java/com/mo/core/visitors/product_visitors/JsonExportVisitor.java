package com.mo.core.visitors.product_visitors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.model.products.*;

@Component
public class JsonExportVisitor implements ProductVisitor<String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String visit(ServiceProduct product) {
        return toJson(product);
    }

    @Override
    public String visit(RealEstateProduct product) {
        return toJson(product);
    }

    private String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON conversion failed", e);
        }
    }

	@Override
	public String visit(VehicleProduct product) {
		return toJson(product);
	}

	@Override
	public String visit(ElectronicProduct product) {
		return toJson(product);
	}

	@Override
	public String visit(FoodProduct product) {
		return toJson(product);
	}

	@Override
	public String visit(FashionProduct product) {
		return toJson(product);
	}
    
}