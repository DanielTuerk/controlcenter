package net.wbz.moba.controlcenter.shared.track.model;


import net.wbz.moba.controlcenter.shared.Identity;

/**
 * DTO which represents a entity from persist layer.
 *
 * @author Daniel Tuerk
 */
@Deprecated
public abstract class AbstractDto implements Identity {

    /**
     * ID from persisted entity or {@code null} for non persisted.
     */
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return java.util.Objects.equals(getInternalId(), that.getInternalId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getInternalId());
    }

    @Override
    public String toString() {
        return "AbstractDto{" + "id=" + id + '}';
    }

    @Deprecated
    private Long getInternalId() {
        return id;
    }
}
