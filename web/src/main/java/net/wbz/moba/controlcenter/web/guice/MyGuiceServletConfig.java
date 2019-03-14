package net.wbz.moba.controlcenter.web.guice;

import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import java.io.File;
import java.util.Properties;
import net.wbz.moba.controlcenter.web.server.ServerModule;
import net.wbz.moba.controlcenter.web.server.web.config.ConfigServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.constrution.BusServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.editor.TrackEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.scenario.ScenarioServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.train.TrainEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.web.viewer.TrackViewerServiceImpl;
import net.wbz.moba.controlcenter.web.shared.EventCache;
import net.wbz.selectrix4java.device.DeviceManager;

/**
 * BusDataConfigurationEntity of the guice context.
 * Injector install the JPA module and
 * the {@link com.google.inject.servlet.ServletModule} for the GWT web context.
 *
 * @author Daniel Tuerk
 */
public class MyGuiceServletConfig extends GuiceServletContextListener {

    public static final String SERVICE_BUS = "bus";
    public static final String SERVICE_CONFIG = "config";
    public static final String SERVICE_CONSTRUCTION = "construction";
    public static final String SERVICE_SCENARIO = "scenario";
    public static final String SERVICE_SCENARIO_EDITOR = "scenarioEditor";
    public static final String SERVICE_TRACK = "track";
    public static final String SERVICE_TRACK_EDITOR = "trackEditor";
    public static final String SERVICE_TRAIN = "train";
    public static final String SERVICE_TRAIN_EDITOR = "trainEditor";
    /**
     * Key for the persistence unit to use in web app. (also db name and directory name)
     */
    private static final String PERSISTENCE_UNIT = "derby_db";
    /**
     * Name of the GWT app.
     */
    private static final String APP_NAME = "ControlCenterApp";

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {

            /**
             * Load the home path for the application from the OS user home.
             * Home will be created if not existing.
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

            @Override
            protected void configureServlets() {
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
                 * Install JPA and delegate all requests by the {@link PersistFilter} to enable transaction
                 * handling by HTTP request.
                 * As result you have to inject the {@link javax.persistence.EntityManager} by the
                 * {@link com.google.inject.Provider}.
                 */
                install(new JpaPersistModule(PERSISTENCE_UNIT).properties(properties));
                filter("/" + APP_NAME + "/*").through(PersistFilter.class);

                /*
                 * Register the GWT services.
                 */
                serve("/" + APP_NAME + "/" + SERVICE_BUS).with(BusServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_CONFIG).with(ConfigServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_CONSTRUCTION).with(ConstructionServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_TRACK).with(TrackViewerServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_TRACK_EDITOR).with(TrackEditorServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_TRAIN).with(TrainServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_TRAIN_EDITOR).with(TrainEditorServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_SCENARIO).with(ScenarioServiceImpl.class);
                serve("/" + APP_NAME + "/" + SERVICE_SCENARIO_EDITOR).with(ScenarioEditorServiceImpl.class);

                install(new ServerModule());
            }

            /**
             * Create and return the {@link DeviceManager}.
             *
             * @return {@link DeviceManager}
             */
            @Provides
            @Singleton
            public DeviceManager deviceManager() {
                return new DeviceManager();
            }

            /**
             * The {@link EventCache} for the server side. Cache all events which are thrown to the
             * clients.
             *
             * @return {@link EventCache}
             */
            @Provides
            @Singleton
            public EventCache serverEventCache() {
                return new EventCache();
            }
        });
    }
}
