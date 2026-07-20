package io.github.danieltuerk.controlcenter.shared.device;

import io.github.danieltuerk.controlcenter.shared.AbstractItemEvent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for modified data of {@link DeviceInfo} or created/deleted entity.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "Device update sent via WebSocket")
@Tag(ref = "websocket")
public class DeviceDataChangedEvent extends AbstractItemEvent {

    public DeviceDataChangedEvent(long deviceId) {
        super(deviceId);
    }

    public DeviceDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "DeviceDataChangedEvent{} " + super.toString();
    }
}
