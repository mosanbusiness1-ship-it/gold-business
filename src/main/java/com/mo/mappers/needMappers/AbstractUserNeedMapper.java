package com.mo.mappers.needMappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;
import com.mo.core.kafka.consumers.MessageConsumer;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.mappers.UserMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper personnalisé pour les entités abstraites de UserNeed.
 * Cette classe combine MapStruct (interface) et du mapping JSON avec ObjectMapper.
 */
@Slf4j
@Component
public class AbstractUserNeedMapper {

    @Autowired private ObjectMapper mapper;

    public AbstractUserNeed toAbstractUserNeed(Map<String, Object> raw) {
        try {
            return mapper.convertValue(raw, AbstractUserNeed.class);
        } catch (IllegalArgumentException e) {
            log.error("Erreur de conversion en AbstractUserNeed : {}", raw, e);
            return null;
        }
    }

    public List<AbstractUserNeed> mapValidNeeds(List<Map<String, Object>> raws) {
        return Optional.ofNullable(raws).orElse(Collections.emptyList())
            .stream()
            .map(this::toAbstractUserNeed)
            .filter(need -> need != null && need.getUser() != null)
            .collect(Collectors.toList());
    }
}