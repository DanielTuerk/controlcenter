package net.wbz.moba.controlcenter.web.guice;

import java.util.Properties;

/**
 * @author Daniel Tuerk
 */
public class EmbeddedDerbyConfiguration implements DatabaseConfiguration {

    private final Properties properties;

    public EmbeddedDerbyConfiguration(String homePath, String databaseFolderName) {
        properties = new Properties();
        // derby
        properties.put("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver");
        properties.put("hibernate.dialect", "net.wbz.moba.controlcenter.web.guice.DerbyDialect");
        // auth
        properties.put("hibernate.connection.url", "jdbc:derby:" + homePath + "/data/" + databaseFolderName
                + ";create=true");
        properties.put("hibernate.connection.username", "");
        properties.put("hibernate.connection.password", "");
        // common
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", "update");
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
