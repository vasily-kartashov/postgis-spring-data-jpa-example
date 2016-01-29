package com.kartashov.postgis.hibernate;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;
import org.hibernate.type.StandardBasicTypes;

public class PostgisWithJsonbDialect extends PostgisDialect {

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        typeContributions.contributeType(PropertiesType.INSTANCE);
    }

    @Override
    protected void registerTypesAndFunctions() {
        super.registerTypesAndFunctions();
        registerColumnType(JsonbTypeDescriptor.INSTANCE.getSqlType(), "jsonb");
        registerFunction("jsonb_extract_path_text",
                new StandardSQLFunction("jsonb_extract_path_text", StandardBasicTypes.STRING));
    }
}
