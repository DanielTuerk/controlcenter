package net.wbz.moba.controlcenter.shared.track.model;


import lombok.Setter;
import net.wbz.moba.controlcenter.shared.Identity;

/**
 * DTO, which represents an entity from a persisting layer.
 *
 * @author Daniel Tuerk
 */
@Setter
public abstract class AbstractDto implements Identity {

    /**
     * ID from persisted entity or {@code null} for non persisted.
     */
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractDto that = (AbstractDto) o;
        return java.util.Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "AbstractDto{" + "id=" + id + '}';
    }

}
