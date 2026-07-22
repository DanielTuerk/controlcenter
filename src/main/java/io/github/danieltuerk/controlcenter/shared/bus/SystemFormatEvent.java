package io.github.danieltuerk.controlcenter.shared.bus;

import io.github.danieltuerk.controlcenter.shared.StateEvent;
import io.github.danieltuerk.selectrix4java.device.Device.SYSTEM_FORMAT;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the state change of the rail voltage.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "system format update sent via WebSocket")
@Tag(ref = "websocket")
public record SystemFormatEvent(SYSTEM_FORMAT systemFormat) implements StateEvent {

}
