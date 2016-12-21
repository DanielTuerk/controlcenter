package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.shared.Identity;

import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public abstract class AbstractDto implements Serializable, Identity {


    private final Long id;

    public AbstractDto() {
        this(null);
    }

    public AbstractDto(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

}
