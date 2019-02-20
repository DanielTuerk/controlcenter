package net.wbz.moba.controlcenter.web.shared;

import net.wbz.moba.controlcenter.web.client.event.StateEvent;

/**
 * @author Daniel Tuerk
 */
public class AbstractStateEvent implements StateEvent {

    public long itemId = -1;

    public AbstractStateEvent() {
    }

    public AbstractStateEvent(long itemId) {
        this.itemId = itemId;
    }

    public long getItemId() {
        return itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractStateEvent that = (AbstractStateEvent) o;
        return itemId == that.itemId;
    }

    @Override
    public int hashCode() {

        return java.util.Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return "AbstractStateEvent{" + "itemId=" + itemId + '}';
    }
}
