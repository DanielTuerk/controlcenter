package io.github.danieltuerk.controlcenter.api.station;

import io.github.danieltuerk.controlcenter.service.station.StationManager;
import io.github.danieltuerk.controlcenter.shared.station.Station;
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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import java.util.List;

@Path("/api/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class StationResource {

    @Inject
    StationManager stationManager;

    @GET
    public List<Station> listAll() {
        return stationManager.load();
    }

    @GET
    @Path("/{id}")
    @APIResponseSchema(Station.class)
    public Response getById(@PathParam("id") Long id) {
        var byId = stationManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @APIResponseSchema(value = Station.class, responseCode = "201")
    public Response create(StationDto created) {
        var station = stationManager.create(created);
        return Response.status(Response.Status.CREATED)
            .entity(station)
            .build();
    }

    @PUT
    @Path("/{id}")
    @APIResponseSchema(Station.class)
    public Response update(@PathParam("id") Long id, StationDto updated) {
        if (!stationManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(stationManager.update(id, updated)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = stationManager.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
