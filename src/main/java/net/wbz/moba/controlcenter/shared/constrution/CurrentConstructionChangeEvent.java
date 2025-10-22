package net.wbz.moba.controlcenter.shared.constrution;

import net.wbz.moba.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "Train status update sent via WebSocket")
@Tag(ref = "websocket")
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
