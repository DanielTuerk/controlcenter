package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.common.base.Objects;
import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.Identity;

import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public abstract class AbstractDto implements Serializable, Identity {

    @JMap
    private Long id;


    public AbstractDto() {
    }

    public AbstractDto(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDto that = (AbstractDto) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
