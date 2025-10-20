package net.wbz.moba.controlcenter.web.server;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;

import com.google.inject.Provides;

import com.google.inject.name.Named;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.persist.jpa.JpaPersistOptions;
import java.io.File;
import java.util.Properties;
import net.wbz.moba.controlcenter.web.guice.db.EmbeddedDerbyConfiguration;
import net.wbz.moba.controlcenter.web.guice.db.RemoteDerbyConfiguration;

/**
 * @author Daniel Tuerk
 */
public class PersistModule extends AbstractModule {

    /**
     * Key for the persistence unit to use in web app. (also db name and directory name)
     */
    private static final String PERSISTENCE_UNIT = "derby_db";

    @Override
    protected void configure() {
        /*
         * Database properties for JPA.
         */
        Properties properties;
        String dbMode = System.getProperty("dbMode");
        if (!Strings.isNullOrEmpty(dbMode) && dbMode.equalsIgnoreCase("remote")) {
            properties = new RemoteDerbyConfiguration().getProperties();
        } else {
            properties = new EmbeddedDerbyConfiguration(homePath(), PERSISTENCE_UNIT).getProperties();
        }

        /*
         * TODO: not up2date
         * Install JPA and delegate all requests by the {@link PersistFilter} to enable transaction
         * handling by HTTP request.
         * As result, you have to inject the {@link javax.persistence.EntityManager} by the
         * {@link com.google.inject.Provider}.
         */
        install(new JpaPersistModule(PERSISTENCE_UNIT,
            // set auto-work-on-unit instead of persist filter (servlet)
            JpaPersistOptions.builder().setAutoBeginWorkOnEntityManagerCreation(true).build())
            .properties(properties));
//                filter("/*").through(PersistFilter.class); TODO?
        // start persist service to make entity manager available in guice // TODO normally done before by PersistFilter
        bind(JPAInitializer.class).asEagerSingleton();
    }

    /**
     * Load the home path for the application from the OS user home. Home will be created if not existing.
     *
     * @return {@link String} absolute file path to home folder
     */
    @Provides
    @Singleton
    @Named("homePath")
    public String homePath() {
        File configPath = new File(System.getProperty("user.home") + "/.moba/");
        if (!configPath.exists()) {
            if (!configPath.mkdirs()) {
                throw new RuntimeException("can't create the HOME path: " + configPath.getAbsolutePath());
            }
        }
        return configPath.getAbsolutePath();
    }

    @Singleton
    private static class JPAInitializer {

        @Inject
        public JPAInitializer(final PersistService service) {
            service.start();
        }
    }
}
