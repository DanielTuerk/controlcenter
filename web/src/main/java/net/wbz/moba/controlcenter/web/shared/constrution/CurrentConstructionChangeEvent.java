package net.wbz.moba.controlcenter.web.shared.constrution;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class CurrentConstructionChangeEvent implements Event {

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
