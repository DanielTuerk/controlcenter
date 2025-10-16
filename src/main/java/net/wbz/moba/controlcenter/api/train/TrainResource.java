package net.wbz.moba.controlcenter.api.train;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
import java.util.List;
import net.wbz.moba.controlcenter.service.train.TrainManager;
import net.wbz.moba.controlcenter.shared.train.Train;

@Path("/trains")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrainResource {

    @Inject
    TrainManager trainManager;

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
    @Transactional
    public Response create(TrainDto dto) {
        var construction = trainManager.create(dto);
        return Response.status(Response.Status.CREATED)
            .entity(construction)
            .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, TrainDto dto) {
        if (!trainManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        trainManager.update(id, dto);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = trainManager.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
