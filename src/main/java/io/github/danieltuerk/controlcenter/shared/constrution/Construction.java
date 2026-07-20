package io.github.danieltuerk.controlcenter.shared.constrution;


import io.github.danieltuerk.controlcenter.shared.track.model.AbstractDto;

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
