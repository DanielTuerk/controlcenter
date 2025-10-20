package net.wbz.moba.controlcenter.api.scenario;

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
import net.wbz.moba.controlcenter.service.scenario.ScenarioManager;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk
 */
@Path("/api/scenarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScenarioController {

    @Inject
    ScenarioManager scenarioManager;

    @GET
    public List<Scenario> listAll() {
        return scenarioManager.getScenarios();
    }


    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var byId = scenarioManager.getById(id);
        if (byId.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(byId.get()).build();
    }

    @POST
    @Transactional
    public Response create(Scenario created) {
        var construction = scenarioManager.createScenario(created);
        return Response.status(Response.Status.CREATED)
            .entity(construction)
            .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Scenario updated) {
        if (!scenarioManager.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        scenarioManager.updateScenario(id, updated);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = scenarioManager.deleteScenario(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
