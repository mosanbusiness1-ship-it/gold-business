package com.mo.mappers.needMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mo.core.dtos.userNeedsDTO.ServiceNeedDto;
import com.mo.core.model.needs.ServiceNeed;
import com.mo.mappers.BaseMapper;
import com.mo.mappers.UserMapper;


@Mapper(
	    componentModel = "spring",
	    unmappedTargetPolicy = ReportingPolicy.WARN,
	    uses = UserMapper.class
	)
public interface ServiceNeedMapper extends BaseMapper<ServiceNeed, ServiceNeedDto> {

	@Mapping(target = "userId", expression = "java(entity.getUser() != null ? entity.getUser().getId() : null)")
	ServiceNeedDto toDto(ServiceNeed entity);

	@Mapping(target = "user", expression = "java(dto.getUserId() != null ? com.mo.auth.User.builder().id(dto.getUserId()).build() : null)")
	ServiceNeed toEntity(ServiceNeedDto dto);
}

