package com.mo.mappers.needMappers;

import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.userNeedsDTO.RealEstateNeedDto;
import com.mo.core.model.needs.RealEstateNeed;
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
public interface RealEstateNeedMapper extends BaseMapper<RealEstateNeed, RealEstateNeedDto> {

	@Mapping(target = "userId", expression = "java(entity.getUser() != null ? entity.getUser().getId() : null)")
	RealEstateNeedDto toDto(RealEstateNeed entity);

	@Mapping(target = "user", expression = "java(dto.getUserId() != null ? com.mo.auth.User.builder().id(dto.getUserId()).build() : null)")
	RealEstateNeed toEntity(RealEstateNeedDto dto);

}