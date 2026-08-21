package com.mo.mappers.productsMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


import com.mo.core.dtos.productsDtos.ElectronicProductDto;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.mappers.BaseMapper;
import com.mo.mappers.UserMapper;
@Mapper(
	    componentModel = "spring",
	    // unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    unmappedTargetPolicy = ReportingPolicy.WARN,
	    uses = UserMapper.class
	)
public interface ElectronicProductMapper extends BaseMapper<ElectronicProduct, ElectronicProductDto> {

		@Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
		public ElectronicProductDto toDto(ElectronicProduct entity);

		@Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
		public ElectronicProduct toEntity(ElectronicProductDto dto);
}