package com.mo.mappers.organisationMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.organisationsDtos.CreateOrganisationDTO;
import com.mo.core.model.organisations.Organisation;
import com.mo.mappers.UserMapper;
@Component
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = UserMapper.class
)
public interface CreateOrganisationMapper extends BaseMapper<Organisation, CreateOrganisationDTO>{

    @Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
    CreateOrganisationDTO toDto(Organisation entity);

    @Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
    Organisation toEntity(CreateOrganisationDTO dto);

}
