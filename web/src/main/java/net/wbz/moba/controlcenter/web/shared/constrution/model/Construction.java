package net.wbz.moba.controlcenter.web.shared.constrution.model;

import java.io.Serializable;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class Construction implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
