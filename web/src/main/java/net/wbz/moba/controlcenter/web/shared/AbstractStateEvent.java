package net.wbz.moba.controlcenter.web.shared;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk
 */
public class AbstractStateEvent implements Event {

    public long itemId=-1;

    public AbstractStateEvent() {
    }

    public AbstractStateEvent(long itemId) {
        this.itemId = itemId;
    }

    public long getItemId() {
        return itemId;
    }

    @Override
    public String toString() {
        return "AbstractStateEvent{" +
                "itemId=" + itemId +
                '}';
    }
}
