package net.wbz.moba.controlcenter.web.jersey;

import net.wbz.moba.controlcenter.web.Main;
import net.wbz.moba.controlcenter.web.resource.APIListingResource;
import net.wbz.moba.controlcenter.web.resource.RestResource;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import org.reflections.Reflections;

/**
 * @author Daniel Tuerk
 */
public class JerseyConfiguration extends ResourceConfig {

    @Inject
    public JerseyConfiguration(ServiceLocator serviceLocator) {
        // Guice injection bridge
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        var guiceInjector = Main.getGuiceInjector();

        guiceBridge.bridgeGuiceInjector(guiceInjector);

        register(APIListingResource.class);
        registerRestResources();
    }

    private void registerRestResources() {
        Reflections reflections = new Reflections(Main.class.getPackage().getName());
        reflections.getTypesAnnotatedWith(RestResource.class).forEach(this::register);
    }

}
