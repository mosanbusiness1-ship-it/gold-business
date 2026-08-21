package com.mo.mappers.needMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.userNeedsDTO.VehicleNeedDto;
import com.mo.core.model.needs.VehicleNeed;
import com.mo.mappers.UserMapper;
// @Component
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN,
    uses = UserMapper.class
)
public interface VehicleNeedMapper extends BaseMapper<VehicleNeed, VehicleNeedDto> {

    @Mapping(target = "userId", expression = "java(entity.getUser() != null ? entity.getUser().getId() : null)")
    VehicleNeedDto toDto(VehicleNeed entity);

    @Mapping(target = "user", expression = "java(dto.getUserId() != null ? com.mo.auth.User.builder().id(dto.getUserId()).build() : null)")
    VehicleNeed toEntity(VehicleNeedDto dto);
}

    


