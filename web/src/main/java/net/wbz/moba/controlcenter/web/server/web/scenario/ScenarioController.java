package net.wbz.moba.controlcenter.web.server.web.scenario;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.util.Collection;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.wbz.moba.controlcenter.web.resource.RestResource;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk
 */
@RestResource
@Path("/api/scenario")
public class ScenarioController {

    private final ScenarioEditorService scenarioEditorService;

    @Inject
    public ScenarioController(ScenarioEditorService scenarioEditorService) {
        this.scenarioEditorService = scenarioEditorService;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Collection<Scenario> loadScenarios() {
        return scenarioEditorService.getScenarios();
    }

    @POST
    public void createScenario(@RequestBody Scenario scenario) {
        scenarioEditorService.createScenario(scenario);
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Scenario loadScenario(@PathParam(value = "id") Long id) {
        return scenarioEditorService.getScenarios()
            .stream()
            .filter(scenario -> scenario.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("no scenario found for id " + id));
    }

    @POST
    @Path("/{id}")
    public void updateScenario(@PathParam(value = "id") Long id, @RequestBody Scenario scenario) {
        scenarioEditorService.updateScenario(scenario);
    }

    @DELETE
    @Path("/{id}")
    public void deleteScenario(@PathParam(value = "id") Long id) {
        scenarioEditorService.deleteScenario(id);
    }
}
