package net.wbz.moba.controlcenter.web.guice.db;

import java.util.Properties;

/**
 * Configuration for the database which provides the {@link Properties}.
 *
 * @author Daniel Tuerk
 */
public interface DatabaseConfiguration {

    /**
     * Get properties for the configuration.
     *
     * @return {@link Properties}
     */
    Properties getProperties();
}
