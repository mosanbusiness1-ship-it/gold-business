package com.mo.mappers.productsMappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.dtos.productsDtos.*;
import com.mo.core.factories.ProductFactory;
import com.mo.core.model.products.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapperJackson {

    private final ObjectMapper objectMapper;
    private final ProductFactory productFactory;
    private final VehicleProductMapper vehicleMapper;
    private final FashionProductMapper fashionMapper;
    private final ElectronicProductMapper electronicMapper;
    private final RealEstateProductMapper realEstateMapper;
    private final ServiceProductMapper serviceMapper;
    private final FoodProductMapper foodMapper;

    public AbstractProduct mapToEntity(JsonNode jsonNode) {
        if (jsonNode == null) {
            throw new IllegalArgumentException("Product JSON must not be null");
        }

        AbstractProductDto dto = objectMapper.convertValue(jsonNode, AbstractProductDto.class);
        return productFactory.create(dto);
    }
    

    public AbstractProductDto mapToDtoObject(AbstractProduct product) {
        if (product == null) {
            return null;
        }

        try {
            return switch (product.getType().name()) {
                case "VEHICLE" -> vehicleMapper.toDto((VehicleProduct) product);
                case "FASHION" -> fashionMapper.toDto((FashionProduct) product);
                case "FOOD" -> foodMapper.toDto((FoodProduct) product);
                case "ELECTRONIC" -> electronicMapper.toDto((ElectronicProduct) product);
                case "REALESTATE" -> realEstateMapper.toDto((RealEstateProduct) product);
                case "SERVICE" -> serviceMapper.toDto((ServiceProduct) product);
                default -> throw new IllegalArgumentException("Type de produit inconnu : " + product.getType());
            };
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Erreur de cast du produit selon son type : " + product.getType(), e);
        }
    }

    public JsonNode mapToDto(AbstractProduct product) {
        if (product == null) {
            return null;
        }

        try {
            return objectMapper.valueToTree(mapToDtoObject(product));
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

}

