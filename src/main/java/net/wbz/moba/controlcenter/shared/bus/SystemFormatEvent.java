package net.wbz.moba.controlcenter.shared.bus;

import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.selectrix4java.device.Device.SYSTEM_FORMAT;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Event for the state change of the rail voltage.
 *
 * @author Daniel Tuerk
 */
@Schema(description = "system format update sent via WebSocket")
@Tag(ref = "websocket")
public class SystemFormatEvent implements StateEvent {

    private SYSTEM_FORMAT systemFormat;

    public SystemFormatEvent() {
    }

    public SystemFormatEvent(SYSTEM_FORMAT systemFormat) {
        this.systemFormat = systemFormat;
    }

    public SYSTEM_FORMAT getSystemFormat() {
        return systemFormat;
    }

    public void setSystemFormat(SYSTEM_FORMAT systemFormat) {
        this.systemFormat = systemFormat;
    }

    @Override
    public String toString() {
        String sb = "SystemFormatEvent{" + "systemFormat=" + systemFormat
            + '}';
        return sb;
    }

}
