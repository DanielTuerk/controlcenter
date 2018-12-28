package net.wbz.moba.controlcenter.web.client.event.scenario;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioDataChangedEvent;

/**
 * {@link RemoteEventListener} for the {@link ScenarioDataChangedEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface ScenarioRemoteListener extends RemoteEventListener<ScenarioDataChangedEvent> {

    @Override
    default void applyEvent(ScenarioDataChangedEvent event) {
        scenarioChanged(event);
    }

    @Override
    default Class<ScenarioDataChangedEvent> getRemoteClass() {
        return ScenarioDataChangedEvent.class;
    }

    void scenarioChanged(ScenarioDataChangedEvent event);
}
