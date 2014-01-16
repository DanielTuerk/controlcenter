package net.wbz.moba.controlcenter.web.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import net.wbz.moba.controlcenter.communication.manager.DeviceManager;
import net.wbz.moba.controlcenter.db.DatabaseFactory;
import net.wbz.moba.controlcenter.web.server.constrution.BusServiceImpl;
import net.wbz.moba.controlcenter.web.server.constrution.ConstructionServiceImpl;
import net.wbz.moba.controlcenter.web.server.editor.TrackEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.scenario.ScenarioEditorServiceImpl;
import net.wbz.moba.controlcenter.web.server.scenario.ScenarioServiceImpl;
import net.wbz.moba.controlcenter.web.server.viewer.TrackViewerServiceImpl;

import java.io.File;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class MyGuiceServletConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        Injector parent = Guice.createInjector(new ServletModule() {

            @Override
            protected void configureServlets() {
                serve("/Test/bus").with(BusServiceImpl.class);
                serve("/Test/construction").with(ConstructionServiceImpl.class);
                serve("/Test/trackviewer").with(TrackViewerServiceImpl.class);
                serve("/Test/trackeditor").with(TrackEditorServiceImpl.class);
                serve("/Test/scenarioservice").with(ScenarioServiceImpl.class);
                serve("/Test/scenarioEditor").with(ScenarioEditorServiceImpl.class);
            }

            @Provides
            @Singleton
            public DeviceManager deviceManager() {
                return new DeviceManager();
            }

            @Provides
            @Singleton
            @Named("construction")
            public DatabaseFactory constructionDatabaseFactory() {
                return new DatabaseFactory(getHomeSubDir("constructions"));
            }

            @Provides
            @Singleton
            @Named("settings")
            public DatabaseFactory settingsDatabaseFactory() {
                return new DatabaseFactory(getHomeSubDir("settings"));
            }

            @Provides
            @Singleton
            @Named("scenario")
            public DatabaseFactory scenarioDatabaseFactory() {
                return new DatabaseFactory(getHomeSubDir("scenario"));
            }

            @Provides
            @Singleton
            public String homePath() {
                File configPath = new File(System.getProperty("user.home") + "/.moba/");
                if (!configPath.exists()) {
                    if (!configPath.mkdirs()) {
                        throw new RuntimeException("can't create the HOME path: " + configPath.getAbsolutePath());
                    }
                }
                return configPath.getAbsolutePath();
            }

            private File getHomeSubDir(String dir) {
                File file = new File(homePath() +"/"+ dir+"/");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new RuntimeException("can't create the HOME path: " + file.getAbsolutePath());
                    }
                }
                return file;
            }
        });
        return parent;
    }

}


