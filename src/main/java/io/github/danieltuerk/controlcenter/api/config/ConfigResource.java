package io.github.danieltuerk.controlcenter.api.config;

import io.github.danieltuerk.controlcenter.service.config.ConfigService;
import io.github.danieltuerk.controlcenter.shared.config.ConfigNotAvailableException;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import java.util.Set;

/**
 * @author Daniel Tuerk
 */
@Path("/api/config")
@Consumes(MediaType.TEXT_PLAIN)
@Blocking
public class ConfigResource {

    @Inject
    ConfigService configService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<ConfigItem> listAll() {
        return configService.findAll();
    }

    @GET
    @Path("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponseSchema(String.class)
    public Response loadValue(@PathParam("key") String key) {
        try {
            return Response.ok(configService.loadValue(key)).build();
        } catch (ConfigNotAvailableException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{key}")
    public Response update(@PathParam("key") String key, String updated) {
        configService.saveValue(key, updated);
        return Response.ok().build();
    }
}
