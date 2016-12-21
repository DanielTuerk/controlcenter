package net.wbz.moba.controlcenter.web.shared.constrution;


import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

import javax.persistence.Entity;

/**
 * @author Daniel Tuerk
 */
public class Construction extends AbstractDto {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
