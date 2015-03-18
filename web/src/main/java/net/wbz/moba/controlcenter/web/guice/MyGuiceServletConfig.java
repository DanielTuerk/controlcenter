package net.wbz.moba.controlcenter.web.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
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

import java.io.File;

/**
 * Configuration of the guice context.
 * Injector contains of the {@link net.wbz.moba.controlcenter.web.guice.DatabaseModule} for JPA access and
 * the {@link com.google.inject.servlet.ServletModule} for the GWT webapp context.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class MyGuiceServletConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new DatabaseModule(),
                new ServletModule() {

                    @Override
                    protected void configureServlets() {
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

                    @Provides
                    @Singleton
                    public DeviceManager deviceManager() {
                        return new DeviceManager();
                    }

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
                });
    }
}


