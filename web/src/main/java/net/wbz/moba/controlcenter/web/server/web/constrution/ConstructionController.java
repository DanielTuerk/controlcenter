package net.wbz.moba.controlcenter.web.server.web.constrution;

import java.util.Collection;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.wbz.moba.controlcenter.web.resource.RestResource;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
@RestResource
@Path("/api/construction")
public class ConstructionController {

    private final ConstructionService constructionService;

    @Inject
    public ConstructionController(ConstructionService constructionService) {
        this.constructionService = constructionService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Collection<Construction> loadConstructions() {
        return constructionService.loadConstructions();
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
            return Response.status(Status.OK.getStatusCode()).build();
        } else {
            throw new NotFoundException("no construction found for id " + id);
        }
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Construction getCurrentConstruction() {
        Construction currentConstruction = constructionService.getCurrentConstruction();
        if (currentConstruction == null) {
            throw new NotFoundException("no current construction selected");
        }
        return currentConstruction;
    }
}
