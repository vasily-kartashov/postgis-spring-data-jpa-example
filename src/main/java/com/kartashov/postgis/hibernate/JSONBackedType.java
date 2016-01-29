package com.kartashov.postgis.hibernate;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;

public abstract class JSONBackedType<T> extends AbstractSingleColumnStandardBasicType<T> {

    public JSONBackedType(JSONBackedTypeDescriptor<T> javaTypeDescriptor) {
        super(JSONTypeDescriptor.INSTANCE, javaTypeDescriptor);
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[] { getJavaTypeDescriptor().getJavaTypeClass().getCanonicalName() };
    }

    @Override
    public String getName() {
        return getJavaTypeDescriptor().getJavaTypeClass().getName();
    }
}
