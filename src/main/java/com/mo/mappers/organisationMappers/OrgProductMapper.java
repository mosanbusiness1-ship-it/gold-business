package com.mo.mappers.organisationMappers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
public interface OrgProductMapper extends BaseMapper<Organisation, OrganisationDTO> {

    @Mapping(target = "productIds", expression = "java(entity.getProducts() == null ? java.util.Collections.emptySet() : entity.getProducts().stream().map(p -> p.getId()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "childrenIds", expression = "java(entity.getChildren() == null ? java.util.Collections.emptyList() : entity.getChildren().stream().map(c -> c.getId()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "needIds", expression = "java(entity.getNeeds() == null ? java.util.Collections.emptySet() : entity.getNeeds().stream().map(n -> n.getId()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "auctionIds", ignore = true)
    OrganisationDTO toDto(Organisation entity);

    Organisation toEntity(OrganisationDTO dto);
}
