package net.wbz.moba.controlcenter.api.config;

import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Set;
import net.wbz.moba.controlcenter.service.config.ConfigService;
import net.wbz.moba.controlcenter.shared.config.ConfigNotAvailableException;

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
