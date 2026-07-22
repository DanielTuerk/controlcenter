package io.github.danieltuerk.controlcenter.api.construction;


import io.github.danieltuerk.controlcenter.service.constrution.ConstructionManager;
import io.github.danieltuerk.controlcenter.service.constrution.ConstructionService;
import io.github.danieltuerk.controlcenter.shared.constrution.Construction;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import java.util.List;
import java.util.Objects;

@Path("/api/constructions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class ConstructionResource {

    @Inject
    ConstructionManager constructionManager;
    @Inject
    ConstructionService constructionService;

    @GET
    public List<Construction> listAll() {
        return constructionManager.load();
    }

    @GET
    @Path("/{id}")
    @APIResponseSchema(Construction.class)
    public Response getById(@PathParam("id") Long id) {
        var byId = constructionManager.getById(id);
        if(byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @APIResponseSchema(value = Construction.class, responseCode = "201")
    public Response create(ConstructionDto created) {
        var construction = constructionManager.create(created);
        return Response.status(Response.Status.CREATED)
            .entity(construction)
            .build();
    }

    @PUT
    @Path("/{id}")
    @APIResponseSchema(Construction.class)
    public Response update(@PathParam("id") Long id, ConstructionDto updated) {
        if (!constructionManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(constructionManager.update(id, updated)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (constructionService.getCurrentConstruction()
            .map(construction -> Objects.equals(construction.getId(), id))
            .orElse(false)) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode(),
                    "can't delete current construction")
                .build();
        }
        boolean deleted = constructionManager.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}

