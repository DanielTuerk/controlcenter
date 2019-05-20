package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.jmapper.annotations.JMap;
import java.util.concurrent.atomic.AtomicLong;
import net.wbz.moba.controlcenter.web.shared.Identity;

/**
 * DTO which represents a entity from persist layer.
 *
 * @author Daniel Tuerk
 */
public abstract class AbstractDto implements Identity, IsSerializable {

    /**
     * Counter for the temporary IDs which are used as default. This ids are negative.
     */
    private static AtomicLong TEMP_ID_COUNT = new AtomicLong(-1);

    /**
     * Temporary id which is used to detect non persisted instance. This id is set for {@code null} values of the {@link
     * #id}. That add the support to use {@link #equals(Object)} and {@link #hashCode()} for non persisted instances.
     */
    private Long tempId;

    /**
     * ID from persisted entity or {@code null} for non persisted.
     */
    @JMap
    private Long id;

    public AbstractDto() {
        this(null);
    }

    public AbstractDto(Long id) {
        this.id = id;
        if (id == null) {
            this.tempId = TEMP_ID_COUNT.getAndDecrement();
        } else {
            this.tempId = null;
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Indicates a new created instance which isn't persisted.
     *
     * @return {@code true} if not persisted one
     */
    public boolean isNew() {
        return id == null && tempId != null;
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

    private Long getInternalId() {
        return isNew() ? tempId : id;
    }
}
