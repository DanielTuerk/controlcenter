package net.wbz.moba.controlcenter.web.guice.db;

import java.sql.Types;
import org.hibernate.dialect.DerbyTenSevenDialect;

/**
 * Custom dialect for the project. Is used in the hibernate properties.

 * @author Daniel Tuerk
 */
public class DerbyDialect extends DerbyTenSevenDialect {

    public DerbyDialect() {
        // fix Derby dialect boolean data type mapping error
        registerColumnType(Types.BOOLEAN, "INTEGER");
    }

}
