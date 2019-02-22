package net.wbz.moba.controlcenter.web.shared;

import de.novanic.eventservice.client.event.Event;

/**
 * Abstract {@link Event} for a item identified by the ID of the item.
 *
 * @author Daniel Tuerk
 */
public class AbstractItemEvent implements Event {

    public long itemId = -1;

    public AbstractItemEvent() {
    }

    public AbstractItemEvent(long itemId) {
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
        AbstractItemEvent that = (AbstractItemEvent) o;
        return itemId == that.itemId;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "itemId=" + itemId + '}';
    }
}
