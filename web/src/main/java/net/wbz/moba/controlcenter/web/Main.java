package net.wbz.moba.controlcenter.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.web.jersey.JerseyBootstrapper;
/**
 * @author Daniel Tuerk
 */
@Slf4j
public class Main {

    @Getter
    private static Injector guiceInjector;

    public static void main(String[] args) {
        guiceInjector = Guice.createInjector(new AppModule());

        JerseyBootstrapper bootstrapper = new JerseyBootstrapper( guiceInjector);
        bootstrapper.setupServer();
        try {
            bootstrapper.startServer();
        } catch (Exception e) {
            log.error("can't start jetty server", e);
        }
    }


}
