package net.wbz.moba.controlcenter.web.shared;

import com.google.common.base.Objects;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class AbstractStateEvent implements Event {

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
        return Objects.hashCode(itemId);
    }

    @Override
    public String toString() {
        return "AbstractStateEvent{" +
                "itemId=" + itemId +
                '}';
    }
}
