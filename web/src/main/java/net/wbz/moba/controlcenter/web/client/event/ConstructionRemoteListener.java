package net.wbz.moba.controlcenter.web.client.event;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.constrution.CurrentConstructionChangeEvent;

/**
 * {@link RemoteEventListener} for the {@link CurrentConstructionChangeEvent}s.
 *
 * @author Daniel Tuerk
 */
public interface ConstructionRemoteListener extends RemoteEventListener<CurrentConstructionChangeEvent> {

    @Override
    default void applyEvent(CurrentConstructionChangeEvent event) {
        currentConstructionChanged(event);
    }

    @Override
    default Class<CurrentConstructionChangeEvent> getRemoteClass() {
        return CurrentConstructionChangeEvent.class;
    }

    void currentConstructionChanged(CurrentConstructionChangeEvent event);
}
