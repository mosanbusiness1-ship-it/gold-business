package com.mo.core.factories;

import org.springframework.stereotype.Component;

import com.mo.core.dtos.productsDtos.AbstractProductDto;
import com.mo.core.dtos.productsDtos.ElectronicProductDto;
import com.mo.core.dtos.productsDtos.FashionProductDto;
import com.mo.core.dtos.productsDtos.FoodProductDto;
import com.mo.core.dtos.productsDtos.RealEstateProductDto;
import com.mo.core.dtos.productsDtos.ServiceProductDto;
import com.mo.core.dtos.productsDtos.VehicleProductDto;
import com.mo.core.model.products.AbstractProduct;
import com.mo.mappers.productsMappers.ElectronicProductMapper;
import com.mo.mappers.productsMappers.FashionProductMapper;
import com.mo.mappers.productsMappers.FoodProductMapper;
import com.mo.mappers.productsMappers.RealEstateProductMapper;
import com.mo.mappers.productsMappers.ServiceProductMapper;
import com.mo.mappers.productsMappers.VehicleProductMapper;

@Component
public class DefaultProductFactory implements ProductFactory {

    private final VehicleProductMapper vehicleMapper;
    private final FashionProductMapper fashionMapper;
    private final ElectronicProductMapper electronicMapper;
    private final RealEstateProductMapper realEstateMapper;
    private final ServiceProductMapper serviceMapper;
    private final FoodProductMapper foodMapper;

    public DefaultProductFactory(VehicleProductMapper vehicleMapper,
                                 FashionProductMapper fashionMapper,
                                 ElectronicProductMapper electronicMapper,
                                 RealEstateProductMapper realEstateMapper,
                                 ServiceProductMapper serviceMapper,
                                 FoodProductMapper foodMapper) {
        this.vehicleMapper = vehicleMapper;
        this.fashionMapper = fashionMapper;
        this.electronicMapper = electronicMapper;
        this.realEstateMapper = realEstateMapper;
        this.serviceMapper = serviceMapper;
        this.foodMapper = foodMapper;
    }

    @Override
    public AbstractProduct create(AbstractProductDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Product DTO must not be null");
        }

        if (dto instanceof VehicleProductDto vehicleDto) {
            return vehicleMapper.toEntity(vehicleDto);
        }
        if (dto instanceof FashionProductDto fashionDto) {
            return fashionMapper.toEntity(fashionDto);
        }
        if (dto instanceof FoodProductDto foodDto) {
            return foodMapper.toEntity(foodDto);
        }
        if (dto instanceof ElectronicProductDto electronicDto) {
            return electronicMapper.toEntity(electronicDto);
        }
        if (dto instanceof RealEstateProductDto realEstateDto) {
            return realEstateMapper.toEntity(realEstateDto);
        }
        if (dto instanceof ServiceProductDto serviceDto) {
            return serviceMapper.toEntity(serviceDto);
        }

        throw new IllegalArgumentException("Unsupported product DTO type: " + dto.getClass().getName());
    }
}
