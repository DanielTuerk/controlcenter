package net.wbz.moba.controlcenter.web.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import net.wbz.moba.controlcenter.web.server.web.WebModule;

/**
 * @author Daniel Tuerk
 */
public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PersistModule());
        install(new WebModule());
    }

    @Provides
    @Singleton
    OpenApiResource getOpenApiResource() {
        OpenApiResource openApiResource = new OpenApiResource();
        openApiResource.setConfigLocation("openapi-configuration.json");
        return openApiResource;
    }

}
