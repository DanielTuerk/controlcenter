package net.wbz.moba.controlcenter.web;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.web.server.web.train.TrainController;
import net.wbz.moba.controlcenter.web.server.ServerModule;

/**
 * @author Daniel Tuerk
 */
@Slf4j
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();

        Names.bindProperties(binder(), initializeConfig());

        install(new ServerModule());

        bind(TrainController.class).asEagerSingleton();
    }

    private Properties initializeConfig() {
        Properties prop = new Properties();

        try {
            prop.load(AppModule.class.getClassLoader().
                // TODO also read from home folder
                getResourceAsStream("config.properties"));
        } catch (IOException e) {
            log.error("can't load config.properties", e);
        }

        return prop;
    }
}
