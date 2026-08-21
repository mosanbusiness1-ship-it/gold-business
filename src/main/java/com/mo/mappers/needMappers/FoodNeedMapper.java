package com.mo.mappers.needMappers;


import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.userNeedsDTO.FoodNeedDto;
import com.mo.core.model.needs.FoodNeed;
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
public interface FoodNeedMapper extends BaseMapper<FoodNeed, FoodNeedDto> {
	@Mapping(target = "userId", source = "user") 
    public FoodNeedDto toDto(FoodNeed entity);
 
	@Mapping(target = "user", expression = "java(dto.getUserId() != null ? com.mo.auth.User.builder().id(dto.getUserId()).build() : null)")
	public FoodNeed toEntity(FoodNeedDto dto);
}

