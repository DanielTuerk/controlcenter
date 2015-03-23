package net.wbz.moba.controlcenter.web.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.hibernate.jpa.HibernateJpaUtil;
import net.sf.gilead.core.serialization.GwtProxySerialization;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;
import net.wbz.moba.controlcenter.web.server.config.ConfigServiceImpl;
import net.wbz.moba.controlcenter.web.server.constrution.BusServiceImpl;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.server.editor.TrackEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.scenario.ScenarioEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.scenario.ScenarioServiceImpl;
import net.wbz.moba.controlcenter.web.server.train.TrainEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.train.TrainServiceImpl;
import net.wbz.moba.controlcenter.web.server.viewer.TrackViewerServiceImpl;
import net.wbz.selectrix4java.device.DeviceManager;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.util.Properties;

/**
 * Configuration of the guice context.
 * Injector install the JPA module and
 * the {@link com.google.inject.servlet.ServletModule} for the GWT web context.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class MyGuiceServletConfig extends GuiceServletContextListener {

    /**
     * Key for the persistence unit to use in web app. (also db name and directory name)
     */
    public static final String PERSISTENCE_UNIT = "derby_db";

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ServletModule() {

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
                        Properties properties = new Properties();
                        // derby
                        properties.put("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver");
                        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
                        // auth
                        properties.put("hibernate.connection.url", "jdbc:derby:" + homePath() + "/data/" + PERSISTENCE_UNIT + ";create=true");
                        properties.put("hibernate.connection.username", "");
                        properties.put("hibernate.connection.password", "");
                        // common
                        properties.put("hibernate.hbm2ddl.auto", "update");

                        /*
                         * Install JPA and delegate all requests by the {@link PersistFilter} to enable transaction handling
                         * by HTTP request. As result you have to inject the {@link javax.persistence.EntityManager}
                         * by the {@link com.google.inject.Provider}.
                          */
                        install(new JpaPersistModule(PERSISTENCE_UNIT).properties(properties));
                        filter("/Test/*").through(PersistFilter.class);

                        /*
                         * Register the GWT services.
                         */
                        serve("/Test/bus").with(BusServiceImpl.class);
                        serve("/Test/construction").with(ConstructionServiceImpl.class);
                        serve("/Test/trackviewer").with(TrackViewerServiceImpl.class);
                        serve("/Test/trackeditor").with(TrackEditorServiceImpl.class);
                        serve("/Test/scenarioservice").with(ScenarioServiceImpl.class);
                        serve("/Test/scenarioEditor").with(ScenarioEditorServiceImpl.class);
                        serve("/Test/trainEditor").with(TrainEditorServiceImpl.class);
                        serve("/Test/trainService").with(TrainServiceImpl.class);
                        serve("/Test/config").with(ConfigServiceImpl.class);
                    }

                    /**
                     * Create and return the {@link net.wbz.selectrix4java.device.DeviceManager}.
                     *
                     * @return {@link net.wbz.selectrix4java.device.DeviceManager}
                     */
                    @Provides
                    @Singleton
                    public DeviceManager deviceManager() {
                        return new DeviceManager();
                    }

                    /**
                     * Create and return the {@link net.sf.gilead.core.PersistentBeanManager} for gilead to use the
                     * configured JPA {@link javax.persistence.EntityManagerFactory} for the GWT services to
                     * serialize the hibernate models as native gwt models.
                     * Each service must ne inherit {@link net.sf.gilead.gwt.PersistentRemoteService} and set the
                     * manager by calling {@link net.sf.gilead.gwt.PersistentRemoteService#setBeanManager(net.sf.gilead.core.PersistentBeanManager)}.
                     *
                     * @param entityManagerFactory {@link javax.persistence.EntityManagerFactory} factory from {@link com.google.inject.persist.jpa.JpaPersistModule}
                     * @return {@link net.sf.gilead.core.PersistentBeanManager}
                     */
                    @Provides
                    @Singleton
                    public PersistentBeanManager persistentBeanManager(EntityManagerFactory entityManagerFactory) {
                        // use JPA repository
                        HibernateJpaUtil hibernateJpaUtil = new HibernateJpaUtil();
                        hibernateJpaUtil.setEntityManagerFactory(entityManagerFactory);
                        PersistentBeanManager persistentBeanManager = new PersistentBeanManager();
                        persistentBeanManager.setPersistenceUtil(hibernateJpaUtil);

                        // serialization of the hibernate entities for GWT
                        StatelessProxyStore proxyStore = new StatelessProxyStore();
                        proxyStore.setProxySerializer(new GwtProxySerialization());
                        persistentBeanManager.setProxyStore(proxyStore);

                        return persistentBeanManager;
                    }

                });
    }
}


