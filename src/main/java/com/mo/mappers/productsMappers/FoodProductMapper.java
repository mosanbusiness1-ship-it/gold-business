package com.mo.mappers.productsMappers;


import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.productsDtos.FoodProductDto;
import com.mo.core.model.products.FoodProduct;
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
public interface FoodProductMapper extends BaseMapper<FoodProduct, FoodProductDto> {
    
		@Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
		public FoodProductDto toDto(FoodProduct entity);
 
        @Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
		public FoodProduct toEntity(FoodProductDto dto);
}

