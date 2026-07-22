package io.github.danieltuerk.controlcenter.api.scenario;

import io.github.danieltuerk.controlcenter.service.scenario.ScenarioManager;
import io.github.danieltuerk.controlcenter.service.scenario.ScenarioService;
import io.github.danieltuerk.controlcenter.service.scenario.statistic.ScenarioStatisticManager;
import io.github.danieltuerk.controlcenter.shared.scenario.Scenario;
import io.github.danieltuerk.controlcenter.shared.scenario.ScenarioStatistic;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

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
    @Path("/{id}/cancel")
    public Response cancel(@PathParam("id") Long id) {
        final var scenario$ = scenarioManager.getScenarioById(id);
        if (scenario$.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        scenarioService.cancel(scenario$.get());
        return Response.ok().build();
    }

    @POST
    @Path("/cancel-all")
    public Response cancelAll() {
        scenarioService.cancelAll();
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/unschedule")
    public Response unschedule(@PathParam("id") Long id) {
        final var scenario$ = scenarioManager.getScenarioById(id);
        if (scenario$.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        scenarioService.unschedule(scenario$.get().getId());
        return Response.ok().build();
    }

    @POST
    @Path("/unschedule-all")
    public Response unscheduleAll() {
        scenarioService.unscheduleAll();
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
