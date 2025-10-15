package net.wbz.moba.controlcenter.web.server.persist;

import com.google.common.base.Objects;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;



import net.wbz.moba.controlcenter.shared.Identity;

/**
 * @author Daniel Tuerk
 */
@MappedSuperclass
public abstract class AbstractEntity implements Identity {

    @Id
    @GeneratedValue
    private Long id;

    public AbstractEntity() {
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractEntity that = (AbstractEntity) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
