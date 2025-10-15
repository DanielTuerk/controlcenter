package net.wbz.moba.controlcenter.api;


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
import java.util.Optional;
import net.wbz.moba.controlcenter.service.ConstructionService;
import net.wbz.moba.controlcenter.shared.constrution.Construction;

@Path("/constructions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConstructionResource {

    @Inject
    ConstructionService constructionService;

    @GET
    public List<Construction> listAll() {
        return constructionService.loadConstructions();
    }

    @GET
    @Path("/{id}")
    public Optional<Construction> getById(@PathParam("id") Long id) {
        // TODO test if optional also result in 404
        return constructionService.getById(id);
    }

    @POST
    @Transactional
    public Response create(ConstructionDto created) {
        var construction = constructionService.createConstruction(created);
        return Response.status(Response.Status.CREATED)
            .entity(construction)
            .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ConstructionDto updated) {
        if (!constructionService.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        constructionService.updateConstruction(id, updated);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = constructionService.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}

