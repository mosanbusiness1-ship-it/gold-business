package com.mo.configuration.mappers;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import org.mapstruct.MappingTarget;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
/**
 *
 * @author douglas
 * @param <E>
 * @param <D>
 */
public interface BaseMapper<E, D> {

    public D toDto(E entity);

    public E toEntity(D dto);

    public List<D> toDtoList(List<E> entityList);

    public List<E> toEntityList(List<D> dtoList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public void update(@MappingTarget E entity, D source);
    
    public default List<D> mapEntityListToDtoList(List<E> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    public default List<E> mapDtoListToEntityList(List<D> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    public default Map<String, String> mapToStringMap(Map<String, Object> input) {
        if (input == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        input.forEach((key, value) -> result.put(key, String.valueOf(value)));
        return result;
    }
    
    @AfterMapping
    default void setTimestamps(@MappingTarget E entity) {
        try {
            Field createdAtField = getField(entity.getClass(), "createdAt");
            if (createdAtField != null && createdAtField.getType().equals(Instant.class)) {
                createdAtField.setAccessible(true);
                if (createdAtField.get(entity) == null) {
                    createdAtField.set(entity, Instant.now());
                }
            }

            Field updatedAtField = getField(entity.getClass(), "updatedAt");
            if (updatedAtField != null && updatedAtField.getType().equals(Instant.class)) {
                updatedAtField.setAccessible(true);
                updatedAtField.set(entity, Instant.now());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set timestamps", e);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), fieldName);
            }
        }
        return null;
    }
}