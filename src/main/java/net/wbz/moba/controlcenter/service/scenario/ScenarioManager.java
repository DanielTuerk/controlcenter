package net.wbz.moba.controlcenter.service.scenario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.shared.AbstractItemEvent;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;
import net.wbz.moba.controlcenter.shared.scenario.ScenarioDataChangedEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class ScenarioManager {

    private final EventBroadcaster eventBroadcaster;
    private final ScenarioMapper dataMapper;
    private final Event<ScenarioDataChangedEvent> scenarioDataChangedEvent;
    private final ScenarioDataProvider dataProvider;

    @Inject
    public ScenarioManager(EventBroadcaster eventBroadcaster, ScenarioMapper dataMapper,
                           Event<ScenarioDataChangedEvent> scenarioDataChangedEvent,
                           ScenarioDataProvider dataProvider) {
        this.eventBroadcaster = eventBroadcaster;
        this.dataMapper = dataMapper;
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
        return dataProvider.getScenarios().stream()
            .map(dataMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public Scenario createScenario(Scenario scenario) {
        final var entity = dataProvider.createScenario(scenario);
        fireEvent(entity.id, AbstractItemEvent.ACTION_TYPE.CREATE);
        return dataMapper.toDto(entity);
    }

    /**
     * Delete the {@link Scenario} for the given id and reload the cached data.
     *
     * @param scenarioId id of {@link Scenario} to delete
     * @return {@code true} if deleted, otherwise {@code false}
     */
    @Transactional
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
    @Transactional
    public Scenario updateScenario(Long scenarioId, Scenario scenario) {
        final var scenarioEntity = dataProvider.updateScenario(scenarioId, scenario);
        fireEvent(scenarioEntity.id, AbstractItemEvent.ACTION_TYPE.UPDATE);
        return dataMapper.toDto(scenarioEntity);
    }

    private void fireEvent(Long id, AbstractItemEvent.ACTION_TYPE type) {
        final var event = new ScenarioDataChangedEvent(id, type);
        scenarioDataChangedEvent.fire(event);
        eventBroadcaster.fireEvent(event);
    }

}