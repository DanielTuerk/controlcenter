package net.wbz.moba.controlcenter.api.construction;


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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import net.wbz.moba.controlcenter.service.constrution.ConstructionManager;
import net.wbz.moba.controlcenter.service.constrution.ConstructionService;
import net.wbz.moba.controlcenter.shared.constrution.Construction;

@Path("/api/current-construction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
public class CurrentConstructionResource {

    @Inject
    ConstructionManager constructionManager;
    @Inject
    ConstructionService constructionService;

    @POST
    public Response setCurrent(Long id) {
        var byId = constructionManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        constructionService.setCurrentConstruction(byId.get());
        return Response.ok(byId.get()).build();
    }

    @GET
    public Response getCurrent() {
        var currentConstruction = constructionService.getCurrentConstruction();
        if (currentConstruction.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(currentConstruction).build();
    }
}

