package net.wbz.moba.controlcenter.api.scenario;

import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.service.scenario.TrackBuilder;
import net.wbz.moba.controlcenter.service.scenario.route.RouteManager;
import net.wbz.moba.controlcenter.shared.scenario.Route;
import net.wbz.moba.controlcenter.shared.scenario.TrackNotFoundException;

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
    public Response getById(@PathParam("id") Long id) {
        var byId = routeManager.getRouteById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @Path("/build-track")
    public Response buildTrack(Route route) {
        try {
            return Response.ok(trackBuilder.build(route)).build();
        } catch (TrackNotFoundException e) {
            log.info("failed to build track: {}", e.getMessage());
            return Response.status(Status.NOT_ACCEPTABLE).build();
        }
    }

    @POST
    public Response create(Route route) {
        return Response.status(Response.Status.CREATED)
            .entity(routeManager.createRoute(route))
            .build();
    }

    @PUT
    @Path("/{id}")
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
