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
    

    public JsonNode mapToDto(AbstractUserNeed need) {
        if (need == null) {
            return null;
        }

        try {
            AbstractUserNeedDto dto = switch (need.getType().name()) {
                case "VEHICLE" -> vehicleMapper.toDto((VehicleNeed) need);
                case "FASHION" -> fashionMapper.toDto((FashionNeed) need);
                case "FOOD" -> foodMapper.toDto((FoodNeed) need);
                case "ELECTRONIC" -> electronicMapper.toDto((ElectronicNeed) need);
                case "REALESTATE" -> realEstateMapper.toDto((RealEstateNeed) need);
                case "SERVICE" -> serviceMapper.toDto((ServiceNeed) need);
                default -> throw new IllegalArgumentException("Type de besoin inconnu : " + need.getType());
            };

            dto.setId(need.getId());
            dto.setTechnicalSheet(need.getTechnicalSheet());
            return objectMapper.valueToTree(dto);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Erreur de cast du besoin selon son type : " + need.getType(), e);
        }
    }

}

