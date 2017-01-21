package net.wbz.moba.controlcenter.web.server.persist;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.Identity;

/**
 * @author Daniel Tuerk
 */
@MappedSuperclass
public abstract class AbstractEntity implements Identity {

    @Id
    @GeneratedValue
    @JMap
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
}
