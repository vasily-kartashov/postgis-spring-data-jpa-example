package com.kartashov.postgis.hibernate;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.postgresql.util.PGobject;

import java.sql.*;

public class JSONTypeDescriptor implements SqlTypeDescriptor {

    public static final JSONTypeDescriptor INSTANCE = new JSONTypeDescriptor();

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
                final String content = getJavaDescriptor().unwrap(value, String.class, options);
                PGobject object = new PGobject();
                object.setType("jsonb");
                object.setValue(content);
                st.setObject(index, object);
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X> (javaTypeDescriptor, this) {

            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                return getJavaDescriptor().wrap(toString(rs.getObject(name)), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return getJavaDescriptor().wrap(toString(statement.getObject(index)), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return getJavaDescriptor().wrap(toString(statement.getObject(name)), options);
            }

            private String toString(Object object) throws SQLException {
                if (object == null) {
                    return null;
                }
                if (object instanceof PGobject) {
                    return ((PGobject) object).getValue();
                } else {
                    throw new SQLException("Received object of type " + object.getClass().getCanonicalName() );
                }
            }
        };
    }
}
