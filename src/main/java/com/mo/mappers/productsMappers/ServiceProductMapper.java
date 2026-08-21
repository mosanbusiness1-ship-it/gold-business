package com.mo.mappers.productsMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mo.core.dtos.productsDtos.ServiceProductDto;
import com.mo.core.model.products.ServiceProduct;
import com.mo.mappers.BaseMapper;
import com.mo.mappers.UserMapper;


@Mapper(
    componentModel = "spring",
    // unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.WARN,
    uses = UserMapper.class
)
public interface ServiceProductMapper extends BaseMapper<ServiceProduct, ServiceProductDto> {

    @Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
    ServiceProductDto toDto(ServiceProduct entity);

        @Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
        ServiceProduct toEntity(ServiceProductDto dto);
}

