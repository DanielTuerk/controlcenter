package net.wbz.moba.controlcenter.web.guice;

import java.util.Properties;

/**
 * @author Daniel Tuerk
 */
public class RemoteDerbyConfiguration implements DatabaseConfiguration {

    private final Properties properties;

    public RemoteDerbyConfiguration() {
        this("localhost", 1527, "APP");
    }

    public RemoteDerbyConfiguration(String host, int port, final String dbName) {
        properties = new Properties();
        // derby
        properties.put("hibernate.connection.driver_class", "org.apache.derby.jdbc.ClientDriver");
        properties.put("hibernate.dialect", "net.wbz.moba.controlcenter.web.guice.DerbyDialect");
        // auth
        properties.put("hibernate.connection.url", "jdbc:derby://" + host + ":" + port + "/" + dbName + ";create=true");
        properties.put("hibernate.connection.username", "APP");
        properties.put("hibernate.connection.password", "APP");
        // common
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", "update");
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
