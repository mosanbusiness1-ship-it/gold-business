package com.mo.mappers.organisationMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.organisationsDtos.CreateOrganisationDTO;
import com.mo.core.dtos.organisationsDtos.CreateOrganisationResponseDTO;
import com.mo.core.model.organisations.Organisation;
import com.mo.mappers.UserMapper;

@Component
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = UserMapper.class
)
public interface CreateOrganisationResponseMapper extends BaseMapper<Organisation, CreateOrganisationResponseDTO> {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "ownerId", expression = "java(entity.getOwner() != null ? entity.getOwner().getId() : null)")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "logoUrl", source = "logoUrl")
    @Mapping(target = "visibility", source = "visibility")
    @Mapping(target = "publicJoin", source = "publicJoin")
    @Mapping(target = "requiresApproval", source = "requiresApproval")
    @Mapping(target = "restrictedToAdminsOnly", source = "restrictedToAdminsOnly")
    @Mapping(target = "verified", source = "verified")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    CreateOrganisationResponseDTO toDto(Organisation entity);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "visibility", source = "visibility")
    @Mapping(target = "publicJoin", source = "publicJoin")
    @Mapping(target = "requiresApproval", source = "requiresApproval")
    @Mapping(target = "restrictedToAdminsOnly", source = "restrictedToAdminsOnly")
    @Mapping(target = "verified", source = "verified")
    @Mapping(target = "owner", expression = "java(dto.getOwnerId() != null ? com.mo.auth.User.builder().id(dto.getOwnerId()).build() : null)")
    Organisation toEntity(CreateOrganisationDTO dto);
}

