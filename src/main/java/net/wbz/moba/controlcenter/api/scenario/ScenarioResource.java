package net.wbz.moba.controlcenter.api.scenario;

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
import net.wbz.moba.controlcenter.service.scenario.ScenarioManager;
import net.wbz.moba.controlcenter.service.scenario.ScenarioService;
import net.wbz.moba.controlcenter.service.scenario.ScenarioStatisticManager;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioStatistic;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Path("/api/scenarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Blocking
public class ScenarioResource {

    private final ScenarioManager scenarioManager;
    private final ScenarioStatisticManager scenarioStatisticManager;
    private final ScenarioService scenarioService;

    @Inject
    public ScenarioResource(ScenarioManager scenarioManager, ScenarioStatisticManager scenarioStatisticManager, ScenarioService scenarioService) {
        this.scenarioManager = scenarioManager;
        this.scenarioStatisticManager = scenarioStatisticManager;
        this.scenarioService = scenarioService;
    }

    @GET
    public List<Scenario> listAll() {
        return scenarioManager.getScenarios();
    }

    @GET
    @Path("/{id}")
    @APIResponseSchema(Scenario.class)
    public Response getById(@PathParam("id") Long id) {
        final var scenario$ = scenarioManager.getScenarioById(id);
        if (scenario$.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(scenario$.get()).build();
    }

    @GET
    @Path("/{id}/statistic")
    @APIResponseSchema(ScenarioStatistic.class)
    public Response scenarioStatistic(@PathParam("id") Long id) {
        final var statistic$ = scenarioStatisticManager.load(id);
        if (statistic$.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(statistic$.get()).build();
    }

    @POST
    @APIResponseSchema(value = Scenario.class, responseCode = "201")
    public Response create(Scenario scenario) {
        return Response.status(Response.Status.CREATED)
            .entity(scenarioManager.createScenario(scenario))
            .build();
    }

    @PUT
    @Path("/{id}")
    @APIResponseSchema(Scenario.class)
    public Response update(@PathParam("id") Long id, Scenario updated) {
        if (scenarioManager.notExistsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(scenarioManager.updateScenario(id, updated)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = scenarioManager.deleteScenario(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/start")
    public Response start(@PathParam("id") Long id) {
        final var scenario$ = scenarioManager.getScenarioById(id);
        if (scenario$.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        scenarioService.start(scenario$.get());
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/stop")
    public Response stop(@PathParam("id") Long id) {
        final var scenario$ = scenarioManager.getScenarioById(id);
        if (scenario$.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        scenarioService.stop(scenario$.get());
        return Response.ok().build();
    }

    @POST
    @Path("/stop-all")
    public Response stopAll() {
        scenarioService.stopAll();
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/schedule")
    public Response schedule(@PathParam("id") Long id) {
        final var scenario$ = scenarioManager.getScenarioById(id);
        if (scenario$.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        scenarioService.schedule(scenario$.get());
        return Response.ok().build();
    }

    @POST
    @Path("/schedule-all")
    public Response scheduleAll() {
        scenarioService.scheduleAll();
        return Response.ok().build();
    }
}
