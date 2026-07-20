package io.github.danieltuerk.controlcenter.shared.bus;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * @author Daniel Tuerk
 */
@Schema(description = "bus data update sent via WebSocket")
@Tag(ref = "websocket")
public record BusDataEvent(int bus, int address, int data) implements StateEvent {

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + bus + "-" + address;
    }

}
