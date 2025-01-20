package net.wbz.moba.controlcenter.web.config;

import com.google.inject.Injector;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Daniel Tuerk
 */
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig(Injector injector) {
        packages("net.wbz.moba.controlcenter.web.resource");
        registerInstances(new H2KToGuiceModule(injector));
    }

}