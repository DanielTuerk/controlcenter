package net.wbz.moba.controlcenter.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.web.config.JerseyConfig;
import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
/**
 * @author Daniel Tuerk
 */
@Slf4j
public class Main {

    private static final URI BASE_URI = URI.create("http://localhost:8080/OpenAPIExample/");
    private static Injector injector;

    public static Injector getInjector() {
        return injector;
    }

    public static void main(String[] args) {
        try {
            injector = Guice.createInjector(new AppModule());
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                BASE_URI,
                new JerseyConfig(injector),
                false);



            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
            server.start();

            System.out.println("Application started.");

            Thread.currentThread().join();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
