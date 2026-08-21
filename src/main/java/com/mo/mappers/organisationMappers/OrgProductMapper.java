package com.mo.mappers.organisationMappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import com.mo.configuration.mappers.BaseMapper;
import com.mo.core.dtos.organisationsDtos.OrganisationDTO;
import com.mo.core.model.organisations.Organisation;

@Component
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrgProductMapper extends BaseMapper<Organisation, OrganisationDTO>{

	OrganisationDTO toDto(Organisation entity);

    Organisation toEntity(OrganisationDTO dto);

}
