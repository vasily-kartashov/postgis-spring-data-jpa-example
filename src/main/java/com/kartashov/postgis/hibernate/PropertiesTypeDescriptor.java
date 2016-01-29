package com.kartashov.postgis.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PropertiesTypeDescriptor extends AbstractTypeDescriptor<Properties> {

    public static PropertiesTypeDescriptor INSTANCE = new PropertiesTypeDescriptor();

    private static final Logger logger = LoggerFactory.getLogger(PropertiesTypeDescriptor.class);

    @SuppressWarnings("unchecked")
    public PropertiesTypeDescriptor() {
        super(Properties.class);
        JavaTypeDescriptorRegistry.INSTANCE.addDescriptor(this);
    }

    @Override
    public String toString(Properties value) {
        try {
            return JsonbTypeDescriptor.objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.warn("Cannot convert map {} to string", e);
            return "{}";
        }
    }

    @Override
    public Properties fromString(String string) {
        try {
            return JsonbTypeDescriptor.objectMapper.readValue(string, JsonbTypeDescriptor.typeReference);
        } catch (IOException e) {
            logger.warn("Cannot read value from {}", string, e);
            return null;
        }
    }

    @Override
    public <X> X unwrap(Properties value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Properties.class.isAssignableFrom(type)) {
            return type.cast(value);
        }
        if (String.class.isAssignableFrom(type)) {
            return type.cast(toString(value));
        }
        throw unknownUnwrap(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> Properties wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof Properties) {
            return (Properties) value;
        }
        if (value instanceof String) {
            return fromString((String) value);
        }
        throw unknownWrap(value.getClass());
    }
}
