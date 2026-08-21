package com.mo.mappers.productsMappers;

import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.productsDtos.RealEstateProductDto;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.mappers.UserMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
	    componentModel = "spring",
	    // unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    unmappedTargetPolicy = ReportingPolicy.WARN,
	    uses = UserMapper.class
	)
public interface RealEstateProductMapper extends BaseMapper<RealEstateProduct, RealEstateProductDto> {

	@Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
	public RealEstateProductDto toDto(RealEstateProduct entity);

	@Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
	public RealEstateProduct toEntity(RealEstateProductDto dto);
}
