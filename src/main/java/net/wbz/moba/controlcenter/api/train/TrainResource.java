package net.wbz.moba.controlcenter.api.train;

import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.wbz.moba.controlcenter.service.train.TrainManager;
import net.wbz.moba.controlcenter.service.train.TrainService;
import net.wbz.moba.controlcenter.shared.train.Train;
import net.wbz.moba.controlcenter.shared.train.Train.DRIVING_DIRECTION;

import java.util.List;

@Path("/api/trains")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class TrainResource {

    @Inject
    TrainManager trainManager;
    @Inject
    TrainService trainService;

    @GET
    public List<Train> listAll() {
        return trainManager.load();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var byId = trainManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    public Response create(TrainDto dto) {
        if (trainManager.getByAddress(dto.address()).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                .entity("train with address %d already exists!".formatted(dto.address()))
                .build();
        }
        return Response.status(Response.Status.CREATED)
            .entity(trainManager.create(dto))
            .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, TrainDto dto) {
        if (!trainManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final var byAddress$ = trainManager.getByAddress(dto.address());
        if (byAddress$.isPresent() && !byAddress$.get().getId().equals(id)) {
            return Response.status(Response.Status.CONFLICT)
                .entity("train with address %d already exists!".formatted(dto.address()))
                .build();
        }
        return Response.ok(trainManager.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = trainManager.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{id}/direction")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateDrivingDirection(@PathParam("id") Long id, DRIVING_DIRECTION drivingDirection) {
        try {
            trainService.toggleDrivingDirection(id, DRIVING_DIRECTION.FORWARD == drivingDirection);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/level")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateDrivingDirection(@PathParam("id") Long id, int level) {
        try {
            trainService.updateDrivingLevel(id, level);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/light")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response toggleLight(@PathParam("id") Long id, boolean state) {
        try {
            trainService.toggleLight(id, state);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/horn")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response toggleHorn(@PathParam("id") Long id, boolean state) {
        try {
            trainService.toggleHorn(id, state);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/functions/{functionId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response toggleFunctionState(@PathParam("id") Long trainId, @PathParam("functionId") Long functionId, boolean state) {
        try {
            trainService.toggleFunctionState(trainId, functionId, state);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }

}
