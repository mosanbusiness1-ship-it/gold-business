package com.mo.mappers.needMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mo.core.dtos.userNeedsDTO.ElectronicNeedDto;
import com.mo.core.model.needs.ElectronicNeed;
import com.mo.mappers.BaseMapper;
import com.mo.mappers.UserMapper;
@Mapper(
	    componentModel = "spring",
	    // unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    unmappedTargetPolicy = ReportingPolicy.WARN,
	    uses = UserMapper.class
	)
public interface ElectronicNeedMapper extends BaseMapper<ElectronicNeed, ElectronicNeedDto> {

	@Mapping(target = "userId", expression = "java(entity.getUser() != null ? entity.getUser().getId() : null)")
	public ElectronicNeedDto toDto(ElectronicNeed entity);
 
	@Mapping(target = "user", expression = "java(dto.getUserId() != null ? com.mo.auth.User.builder().id(dto.getUserId()).build() : null)")
    public ElectronicNeed toEntity(ElectronicNeedDto dto);
}