package com.kartashov.postgis.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.*;

public class JsonbTypeDescriptor implements SqlTypeDescriptor {

    public static final JsonbTypeDescriptor INSTANCE = new JsonbTypeDescriptor();

    static final ObjectMapper objectMapper = new ObjectMapper();
    static final Class<Properties> typeReference = Properties.class;

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public boolean canBeRemapped() {
        return false;
    }

    @Override
    public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X> (javaTypeDescriptor, this) {

            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                final Properties properties = getJavaDescriptor().unwrap(value, Properties.class, options);
                try {
                    PGobject object = new PGobject();
                    object.setType("jsonb");
                    object.setValue(objectMapper.writeValueAsString(properties));
                    st.setObject(index, object);
                } catch (JsonProcessingException e) {
                    throw new SQLException(e);
                }
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X> (javaTypeDescriptor, this) {

            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                return getJavaDescriptor().wrap(toProperties(rs.getObject(name)), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return getJavaDescriptor().wrap(toProperties(statement.getObject(index)), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return getJavaDescriptor().wrap(toProperties(statement.getObject(name)), options);
            }

            private Properties toProperties(Object object) throws SQLException {
                if (object == null) {
                    return null;
                }
                if (object instanceof PGobject) {
                    String pgValue = ((PGobject) object).getValue();
                    try {
                        return objectMapper.readValue(pgValue, typeReference);
                    } catch (IOException e) {
                        throw new SQLException(e);
                    }
                } else {
                    throw new SQLException("Received object of type " + object.getClass().getCanonicalName() );
                }
            }
        };
    }
}
