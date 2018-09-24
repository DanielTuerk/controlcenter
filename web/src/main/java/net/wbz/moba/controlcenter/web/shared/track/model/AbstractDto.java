package net.wbz.moba.controlcenter.web.shared.track.model;

import com.googlecode.jmapper.annotations.JMap;
import java.io.Serializable;
import net.wbz.moba.controlcenter.web.shared.Identity;

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
    public String toString() {
        final StringBuffer sb = new StringBuffer("AbstractDto{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
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
        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return java.util.Objects.hash(id);
    }
}
