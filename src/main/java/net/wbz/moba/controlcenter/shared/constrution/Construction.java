package net.wbz.moba.controlcenter.shared.constrution;


import net.wbz.moba.controlcenter.shared.track.model.AbstractDto;

/**
 * @author Daniel Tuerk
 */
public class Construction extends AbstractDto {
    private String name;


    private boolean inAutomaticMode = false;

    public Construction() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInAutomaticMode() {
        return inAutomaticMode;
    }

    public void setInAutomaticMode(boolean inAutomaticMode) {
        this.inAutomaticMode = inAutomaticMode;
    }
}
