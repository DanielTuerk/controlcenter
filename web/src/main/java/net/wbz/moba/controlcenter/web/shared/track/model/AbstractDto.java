package net.wbz.moba.controlcenter.web.shared.track.model;

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
}
