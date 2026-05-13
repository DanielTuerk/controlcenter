package net.wbz.moba.controlcenter.shared.device;

import net.wbz.moba.controlcenter.shared.StateEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the connection state of a device.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "bus status update sent via WebSocket")
@Tag(ref = "websocket")
public record DeviceConnectionEvent(DeviceInfo deviceInfo, boolean connected) implements StateEvent {

    @Override
    public String getCacheKey() {
        return getClass().getSimpleName() + ":" + deviceInfo.getKey();
    }

}
