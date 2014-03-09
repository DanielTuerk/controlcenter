package net.wbz.moba.controlcenter.web.shared;

import de.novanic.eventservice.client.event.Event;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class AbstractStateEvent implements Event {

    public long itemId;

    public AbstractStateEvent() {
    }

    public AbstractStateEvent(long itemId) {
        this.itemId = itemId;
    }

    public long getItemId() {
        return itemId;
    }

}
