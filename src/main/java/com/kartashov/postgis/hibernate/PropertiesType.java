package com.kartashov.postgis.hibernate;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;

public class PropertiesType extends AbstractSingleColumnStandardBasicType<Properties> {

    public static final PropertiesType INSTANCE = new PropertiesType();

    public PropertiesType() {
        super(JsonbTypeDescriptor.INSTANCE, PropertiesTypeDescriptor.INSTANCE);
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[] { Properties.class.getCanonicalName() };
    }

    @Override
    public String getName() {
        return "properties";
    }
}
