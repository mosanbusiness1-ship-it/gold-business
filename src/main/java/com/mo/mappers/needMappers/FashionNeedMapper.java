package com.mo.mappers.needMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.mo.core.dtos.userNeedsDTO.FashionNeedDto;
import com.mo.core.model.needs.FashionNeed;
import com.mo.mappers.BaseMapper;
import com.mo.mappers.UserMapper;
@Mapper(
	    componentModel = "spring",
	    // unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    unmappedTargetPolicy = ReportingPolicy.WARN,
	    uses = UserMapper.class
	)
public interface FashionNeedMapper extends BaseMapper<FashionNeed, FashionNeedDto> {
    // Les méthodes sont héritées de BaseMapper:
	@Mapping(target = "userId", expression = "java(entity.getUser() != null ? entity.getUser().getId() : null)")
	FashionNeedDto toDto(FashionNeed entity);

	@Mapping(target = "user", expression = "java(dto.getUserId() != null ? com.mo.auth.User.builder().id(dto.getUserId()).build() : null)")
	FashionNeed toEntity(FashionNeedDto dto);
}
