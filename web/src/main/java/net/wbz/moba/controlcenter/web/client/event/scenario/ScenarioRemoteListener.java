package net.wbz.moba.controlcenter.web.client.event.scenario;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.scenario.ScenariosChangedEvent;

/**
 * {@link RemoteEventListener} for the {@link ScenariosChangedEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface ScenarioRemoteListener extends RemoteEventListener<ScenariosChangedEvent> {

    @Override
    default void applyEvent(ScenariosChangedEvent event) {
        scenarioChanged(event);
    }

    @Override
    default Class<ScenariosChangedEvent> getRemoteClass() {
        return ScenariosChangedEvent.class;
    }

    void scenarioChanged(ScenariosChangedEvent event);
}
