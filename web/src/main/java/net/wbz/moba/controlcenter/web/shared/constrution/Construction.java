package net.wbz.moba.controlcenter.web.shared.constrution;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class Construction extends AbstractDto {
    @JMap
    private String name;

    public Construction() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
