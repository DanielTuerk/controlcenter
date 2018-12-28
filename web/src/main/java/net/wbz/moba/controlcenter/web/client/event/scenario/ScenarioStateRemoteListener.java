package net.wbz.moba.controlcenter.web.client.event.scenario;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenarioStateEvent;

/**
 * {@link RemoteEventListener} for the {@link ScenarioStateEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface ScenarioStateRemoteListener extends RemoteEventListener<ScenarioStateEvent> {

    @Override
    default void applyEvent(ScenarioStateEvent event) {
        scenarioStateChanged(event);
    }

    @Override
    default Class<ScenarioStateEvent> getRemoteClass() {
        return ScenarioStateEvent.class;
    }

    void scenarioStateChanged(ScenarioStateEvent event);
}
