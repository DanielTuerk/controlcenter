package net.wbz.moba.controlcenter.web.guice.db;

import java.util.Properties;

/**
 * @author Daniel Tuerk
 */
public class RemoteDerbyConfiguration implements DatabaseConfiguration {

    private final Properties properties;

    public RemoteDerbyConfiguration() {
        this("localhost", 1527, "APP", "APP", "APP");
    }

    public RemoteDerbyConfiguration(String host, int port, final String dbName, String user, String password) {
        properties = new Properties();
        // derby
        properties.put("hibernate.connection.driver_class", "org.apache.derby.jdbc.ClientDriver");
        properties.put("hibernate.dialect", DerbyDialect.class.getName());
        // auth
        properties.put("hibernate.connection.url", "jdbc:derby://" + host + ":" + port + "/" + dbName + ";create=true");
        properties.put("hibernate.connection.username", user);
        properties.put("hibernate.connection.password", password);
        // common
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", "update");
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
