package com.kartashov.postgis.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JSONBackedTypeDescriptor<T> extends AbstractTypeDescriptor<T> {

    private static final Logger logger = LoggerFactory.getLogger(JSONBackedTypeDescriptor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JSONBackedTypeDescriptor(Class<T> type) {
        super(type);
        JavaTypeDescriptorRegistry.INSTANCE.addDescriptor(this);
    }

    @Override
    public String toString(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.warn("Cannot convert map {} to string", e);
            return "{}";
        }
    }

    @Override
    public T fromString(String string) {
        try {
            return objectMapper.readValue(string, getJavaTypeClass());
        } catch (IOException e) {
            logger.warn("Cannot read value from {}", string, e);
            return null;
        }
    }

    @Override
    public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }
        if (String.class.isAssignableFrom(type)) {
            return type.cast(toString(value));
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> T wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value.getClass().isAssignableFrom(getJavaTypeClass())) {
            return getJavaTypeClass().cast(value);
        }
        if (value instanceof String) {
            return fromString((String) value);
        }
        throw unknownWrap(value.getClass());
    }
}
