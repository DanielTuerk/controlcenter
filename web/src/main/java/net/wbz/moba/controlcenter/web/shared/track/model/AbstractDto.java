package net.wbz.moba.controlcenter.web.shared.track.model;

import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
public abstract class AbstractDto implements Serializable {


    private final Long id;

    public AbstractDto() {
        this(null);
    }
    public AbstractDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
