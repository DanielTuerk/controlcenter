package net.wbz.moba.controlcenter.web.shared.constrution;

import net.wbz.moba.controlcenter.web.client.event.StateEvent;

/**
 * @author Daniel Tuerk
 */
public class CurrentConstructionChangeEvent implements StateEvent {

    private Construction construction;

    public CurrentConstructionChangeEvent() {
    }

    public CurrentConstructionChangeEvent(Construction construction) {
        this.construction = construction;
    }

    public Construction getConstruction() {
        return construction;
    }

    public void setConstruction(Construction construction) {
        this.construction = construction;
    }
}
