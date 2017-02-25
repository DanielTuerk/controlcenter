package net.wbz.moba.controlcenter.web.guice;

import java.sql.Types;
import org.hibernate.dialect.DerbyTenSevenDialect;

/**
 * @author Daniel Tuerk
 */
public class DerbyDialect extends DerbyTenSevenDialect {

    public DerbyDialect() {
        // fix Derby dialect boolean data type mapping error
        registerColumnType(Types.BOOLEAN, "INTEGER");
    }

}
