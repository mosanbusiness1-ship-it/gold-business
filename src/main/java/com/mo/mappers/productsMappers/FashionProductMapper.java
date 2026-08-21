package com.mo.mappers.productsMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mo.core.dtos.productsDtos.FashionProductDto;
import com.mo.core.model.products.FashionProduct;
import com.mo.mappers.BaseMapper;
import com.mo.mappers.UserMapper;
@Mapper(
	    componentModel = "spring",
	    // unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    unmappedTargetPolicy = ReportingPolicy.WARN,
	    uses = UserMapper.class
	)
public interface FashionProductMapper extends BaseMapper<FashionProduct, FashionProductDto> {

    @Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
    FashionProductDto toDto(FashionProduct entity);

    @Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
    FashionProduct toEntity(FashionProductDto dto);
}
