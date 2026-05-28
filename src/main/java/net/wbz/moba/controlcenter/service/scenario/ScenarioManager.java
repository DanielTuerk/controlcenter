package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioDataChangedEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioManager {

    private final EventBroadcaster eventBroadcaster;

    private final Event<ScenarioDataChangedEvent> scenarioDataChangedEvent;
    private final ScenarioDataProvider dataProvider;

    @Inject
    public ScenarioManager(EventBroadcaster eventBroadcaster,
                           Event<ScenarioDataChangedEvent> scenarioDataChangedEvent,
                           ScenarioDataProvider dataProvider) {
        this.eventBroadcaster = eventBroadcaster;
        this.scenarioDataChangedEvent = scenarioDataChangedEvent;
        this.dataProvider = dataProvider;
    }

    public boolean notExistsById(Long id) {
        return getScenarioById(id).isEmpty();
    }

    public Optional<Scenario> getScenarioById(Long scenarioId) {
        for (Scenario scenario : getScenarios()) {
            if (scenarioId.equals(scenario.getId())) {
                return Optional.of(scenario);
            }
        }
        return Optional.empty();
    }

    public List<Scenario> getScenarios() {
        return dataProvider.getScenarios();
    }

    public Scenario createScenario(Scenario scenario) {
        final var created = dataProvider.createScenario(scenario);
        fireEvent(created.getId(), AbstractItemEvent.ACTION_TYPE.CREATE);
        return created;
    }

    /**
     * Delete the {@link Scenario} for the given id and reload the cached data.
     *
     * @param scenarioId id of {@link Scenario} to delete
     * @return {@code true} if deleted, otherwise {@code false}
     */
    public boolean deleteScenario(Long scenarioId) {
        if (dataProvider.deleteScenario(scenarioId)) {
            fireEvent(scenarioId, AbstractItemEvent.ACTION_TYPE.DELETE);
            return true;
        }
        return false;
    }

    /**
     * Update the given {@link Scenario} and reload the cached data.
     *
     * @param scenario {@link Scenario} to update in database
     */
    public Scenario updateScenario(Long scenarioId, Scenario scenario) {
        final var updated = dataProvider.updateScenario(scenarioId, scenario);
        fireEvent(updated.getId(), AbstractItemEvent.ACTION_TYPE.UPDATE);
        return updated;
    }

    private void fireEvent(Long id, AbstractItemEvent.ACTION_TYPE type) {
        final var event = new ScenarioDataChangedEvent(id, type);
        scenarioDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

}