package io.github.danieltuerk.controlcenter.api.construction;


import io.github.danieltuerk.controlcenter.service.constrution.ConstructionManager;
import io.github.danieltuerk.controlcenter.service.constrution.ConstructionService;
import io.github.danieltuerk.controlcenter.shared.constrution.Construction;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

@Path("/api/current-construction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
@Blocking
public class CurrentConstructionResource {

    @Inject
    ConstructionManager constructionManager;
    @Inject
    ConstructionService constructionService;

    @POST
    @APIResponseSchema(Construction.class)
    public Response setCurrent(Long id) {
        var byId = constructionManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        constructionService.setCurrentConstruction(byId.get());
        return Response.ok(byId.get()).build();
    }

    @GET
    @APIResponseSchema(Construction.class)
    public Response getCurrent() {
        var currentConstruction = constructionService.getCurrentConstruction();
        if (currentConstruction.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(currentConstruction).build();
    }
}

