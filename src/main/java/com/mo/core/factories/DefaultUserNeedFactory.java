package com.mo.core.factories;

import org.springframework.stereotype.Component;

import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;
import com.mo.core.dtos.userNeedsDTO.ElectronicNeedDto;
import com.mo.core.dtos.userNeedsDTO.FashionNeedDto;
import com.mo.core.dtos.userNeedsDTO.FoodNeedDto;
import com.mo.core.dtos.userNeedsDTO.RealEstateNeedDto;
import com.mo.core.dtos.userNeedsDTO.ServiceNeedDto;
import com.mo.core.dtos.userNeedsDTO.VehicleNeedDto;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.mappers.needMappers.ElectronicNeedMapper;
import com.mo.mappers.needMappers.FashionNeedMapper;
import com.mo.mappers.needMappers.FoodNeedMapper;
import com.mo.mappers.needMappers.RealEstateNeedMapper;
import com.mo.mappers.needMappers.ServiceNeedMapper;
import com.mo.mappers.needMappers.VehicleNeedMapper;

@Component
public class DefaultUserNeedFactory implements UserNeedFactory {

    private final VehicleNeedMapper vehicleMapper;
    private final FashionNeedMapper fashionMapper;
    private final ElectronicNeedMapper electronicMapper;
    private final RealEstateNeedMapper realEstateMapper;
    private final ServiceNeedMapper serviceMapper;
    private final FoodNeedMapper foodMapper;

    public DefaultUserNeedFactory(VehicleNeedMapper vehicleMapper,
                                  FashionNeedMapper fashionMapper,
                                  ElectronicNeedMapper electronicMapper,
                                  RealEstateNeedMapper realEstateMapper,
                                  ServiceNeedMapper serviceMapper,
                                  FoodNeedMapper foodMapper) {
        this.vehicleMapper = vehicleMapper;
        this.fashionMapper = fashionMapper;
        this.electronicMapper = electronicMapper;
        this.realEstateMapper = realEstateMapper;
        this.serviceMapper = serviceMapper;
        this.foodMapper = foodMapper;
    }

    @Override
    public AbstractUserNeed create(AbstractUserNeedDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("User need DTO must not be null");
        }

        if (dto instanceof VehicleNeedDto vehicleDto) {
            return vehicleMapper.toEntity(vehicleDto);
        }
        if (dto instanceof FashionNeedDto fashionDto) {
            return fashionMapper.toEntity(fashionDto);
        }
        if (dto instanceof FoodNeedDto foodDto) {
            return foodMapper.toEntity(foodDto);
        }
        if (dto instanceof ElectronicNeedDto electronicDto) {
            return electronicMapper.toEntity(electronicDto);
        }
        if (dto instanceof RealEstateNeedDto realEstateDto) {
            return realEstateMapper.toEntity(realEstateDto);
        }
        if (dto instanceof ServiceNeedDto serviceDto) {
            return serviceMapper.toEntity(serviceDto);
        }

        throw new IllegalArgumentException("Unsupported user need DTO type: " + dto.getClass().getName());
    }
}
