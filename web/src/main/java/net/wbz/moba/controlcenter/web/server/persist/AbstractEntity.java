package net.wbz.moba.controlcenter.web.server.persist;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.shared.Identity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Daniel Tuerk
 */
public class AbstractEntity implements Identity {

    @Id
    @GeneratedValue
    @JMap
    private long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
