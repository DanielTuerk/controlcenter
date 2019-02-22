package net.wbz.moba.controlcenter.web.shared;

import net.wbz.moba.controlcenter.web.client.event.StateEvent;

/**
 * Event for items which is stateful.
 *
 * @author Daniel Tuerk
 */
public class AbstractItemStateEvent extends AbstractItemEvent implements StateEvent {

    public AbstractItemStateEvent() {
    }

    public AbstractItemStateEvent(long itemId) {
        super(itemId);
    }

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + itemId;
    }


    @Override
    public String toString() {
        return "AbstractItemStateEvent{" + "itemId=" + itemId + '}';
    }
}
