package net.wbz.moba.controlcenter.web.config;

import com.google.inject.Injector;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import net.wbz.moba.controlcenter.web.resource.TrainController;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * @author Daniel Tuerk
 */
public class H2KToGuiceModule extends AbstractBinder {

    private Injector injector;

    H2KToGuiceModule(Injector injector) {
        this.injector = injector;
    }

    @Override
    protected void configure() {
        bind(injector.getInstance(OpenApiResource.class)).to(OpenApiResource.class);
        bind(injector.getInstance(TrainController.class)).to(TrainController.class); //TODO can be done by reflection?
    }
}
