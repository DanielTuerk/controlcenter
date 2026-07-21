package io.github.danieltuerk.controlcenter.shared;

import lombok.Getter;

/**
 * Abstract {@link Event} for an item identified by the ID of the item.
 *
 * @author Daniel Tuerk
 */
@Getter
public class AbstractItemEvent implements Event {
    public enum ACTION_TYPE {CREATE, UPDATE, DELETE}

    public long itemId = -1;

    public AbstractItemEvent() {
    }

    public AbstractItemEvent(long itemId) {
        this.itemId = itemId;
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
