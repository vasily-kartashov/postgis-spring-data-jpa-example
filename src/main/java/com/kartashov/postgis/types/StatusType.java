package com.kartashov.postgis.types;

import com.kartashov.postgis.entities.Status;
import com.kartashov.postgis.hibernate.JSONBackedType;
import com.kartashov.postgis.hibernate.JSONBackedTypeDescriptor;

public class StatusType extends JSONBackedType<Status> {

    private final static JSONBackedTypeDescriptor<Status> descriptor = new JSONBackedTypeDescriptor<>(Status.class);

    public StatusType() {
        super(descriptor);
    }
}
