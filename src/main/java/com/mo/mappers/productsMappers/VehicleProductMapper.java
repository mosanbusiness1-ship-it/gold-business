package com.mo.mappers.productsMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.productsDtos.VehicleProductDto;
import com.mo.core.model.products.VehicleProduct;
import com.mo.mappers.UserMapper;
@Component
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = UserMapper.class
)
public interface VehicleProductMapper extends BaseMapper<VehicleProduct, VehicleProductDto> {

    @Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
    VehicleProductDto toDto(VehicleProduct entity);

    @Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
    VehicleProduct toEntity(VehicleProductDto dto);
}

    


