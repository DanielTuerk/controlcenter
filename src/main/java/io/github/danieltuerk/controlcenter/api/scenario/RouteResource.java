package io.github.danieltuerk.controlcenter.api.scenario;

import io.github.danieltuerk.controlcenter.service.scenario.TrackBuilder;
import io.github.danieltuerk.controlcenter.service.scenario.route.RouteManager;
import io.github.danieltuerk.controlcenter.shared.scenario.Route;
import io.github.danieltuerk.controlcenter.shared.scenario.Track;
import io.github.danieltuerk.controlcenter.shared.scenario.TrackNotFoundException;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@Path("/api/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class RouteResource {

    @Inject
    RouteManager routeManager;
    @Inject
    TrackBuilder trackBuilder;

    @GET
    public List<Route> listAll() {
        return routeManager.getRoutes();
    }

    @GET
    @Path("/{id}")
    @APIResponseSchema(Route.class)
    public Response getById(@PathParam("id") Long id) {
        var byId = routeManager.getRouteById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @Path("/build-track")
    @APIResponseSchema(Track.class)
    public Response buildTrack(Route route) {
        try {
            return Response.ok(trackBuilder.build(route)).build();
        } catch (TrackNotFoundException e) {
            log.info("failed to build track: {}", e.getMessage());
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }
    }

    @POST
    @APIResponseSchema(value = Route.class, responseCode = "201")
    public Response create(Route route) {
        return Response.status(Response.Status.CREATED)
            .entity(routeManager.createRoute(route))
            .build();
    }

    @PUT
    @Path("/{id}")
    @APIResponseSchema(Route.class)
    public Response update(@PathParam("id") Long id, Route updated) {
        if (routeManager.notExistsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(routeManager.updateRoute(id, updated)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (routeManager.notExistsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        boolean deleted = routeManager.deleteRoute(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }
    }
}
