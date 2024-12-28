package net.wbz.moba.controlcenter.web.server.web.constrution;

import com.google.gson.GsonBuilder;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;
import net.wbz.moba.controlcenter.web.shared.constrution.ConstructionService;

/**
 * @author Daniel Tuerk
 */
@Path("/api/construction")
public class ConstructionController {

    private final ConstructionService constructionService;

    @Inject
    public ConstructionController(ConstructionService constructionService) {
        this.constructionService = constructionService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String loadConstructions() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(
            constructionService.loadConstructions()
        );
    }

    @POST
    @Path("/current")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response setCurrentConstruction(String id) {
        Optional<Construction> optionalConstruction = constructionService.loadConstructions()
            .stream()
            .filter(c -> c.getId().equals(Long.valueOf(id.trim())))
            .findFirst();
        if (optionalConstruction.isPresent()) {
            constructionService.setCurrentConstruction(optionalConstruction.get());
        } else {
            throw new IllegalArgumentException("no construction found for id " + id);
        }
        return Response.status(Status.OK.getStatusCode()).build();
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCurrentConstruction() {
        Construction currentConstruction = constructionService.getCurrentConstruction();
        return currentConstruction == null ? null
            : new GsonBuilder().setPrettyPrinting().create().toJson(currentConstruction);
    }
}
