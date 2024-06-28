package com.naeem.blogs.domain.core;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public abstract class AbstractEnumConverter<T extends Enum<T> & PersistableEnum<E>, E> implements AttributeConverter<T, E> {
    private final Class<T> clazz;

    protected AbstractEnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public T convertToEntityAttribute(E dbData) {
        T[] enums = clazz.getEnumConstants();

		if(dbData == null) {
       		return null;
        }
        
        for (T e : enums) {
            if (e.getValue().equals(dbData)) {
                return e;
            }
        }
        
        throw new UnsupportedOperationException();
    }
}
