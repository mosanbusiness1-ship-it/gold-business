package com.mo.mappers.needMappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.dtos.userNeedsDTO.*;
import com.mo.core.factories.UserNeedFactory;
import com.mo.core.model.needs.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NeedMapperJackson {

    private final ObjectMapper objectMapper;
    private final UserNeedFactory userNeedFactory;
    private final VehicleNeedMapper vehicleMapper;
    private final FashionNeedMapper fashionMapper;
    private final ElectronicNeedMapper electronicMapper;
    private final RealEstateNeedMapper realEstateMapper;
    private final ServiceNeedMapper serviceMapper;
    private final FoodNeedMapper foodMapper;

    public AbstractUserNeed mapToEntity(JsonNode jsonNode) {
        if (jsonNode == null) {
            throw new IllegalArgumentException("Need JSON must not be null");
        }

        AbstractUserNeedDto dto = objectMapper.convertValue(jsonNode, AbstractUserNeedDto.class);
        return userNeedFactory.create(dto);
    }
    

    public JsonNode mapToDto(AbstractUserNeed Need) {
        if (Need == null) {
            return null;
        }

        try {
            return switch (Need.getType().name()) {
                case "VEHICLE" -> {
                    VehicleNeedDto dto = vehicleMapper.toDto((VehicleNeed) Need);
                    yield objectMapper.valueToTree(dto);
                }
                case "FASHION" -> {
                    FashionNeedDto dto = fashionMapper.toDto((FashionNeed) Need);
                    yield objectMapper.valueToTree(dto);
                }
                case "FOOD" -> {
                    FoodNeedDto dto = foodMapper.toDto((FoodNeed) Need);
                    yield objectMapper.valueToTree(dto);
                }
                case "ELECTRONIC" -> {
                    ElectronicNeedDto dto = electronicMapper.toDto((ElectronicNeed) Need);
                    yield objectMapper.valueToTree(dto);
                }
                case "REALESTATE" -> {
                    RealEstateNeedDto dto = realEstateMapper.toDto((RealEstateNeed) Need);
                    yield objectMapper.valueToTree(dto);
                }
                case "SERVICE" -> {
                    ServiceNeedDto dto = serviceMapper.toDto((ServiceNeed) Need);
                    yield objectMapper.valueToTree(dto);
                }
                default -> throw new IllegalArgumentException("Type de produit inconnu : " + Need.getType());
            };
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Erreur de cast du produit selon son type : " + Need.getType(), e);
        }
    }

}

