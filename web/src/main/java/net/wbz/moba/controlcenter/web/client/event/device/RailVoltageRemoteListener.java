package net.wbz.moba.controlcenter.web.client.event.device;

import net.wbz.moba.controlcenter.web.client.event.RemoteEventListener;
import net.wbz.moba.controlcenter.web.shared.viewer.RailVoltageEvent;

/**
 * Listener for the railvoltage state of the device.
 *
 * @author Daniel Tuerk
 */
public interface RailVoltageRemoteListener extends RemoteEventListener<RailVoltageEvent> {

    @Override
    default void applyEvent(RailVoltageEvent event) {
        if (event.isState()) {
            on();
        } else {
            off();
        }
    }

    @Override
    default Class<RailVoltageEvent> getRemoteClass() {
        return RailVoltageEvent.class;
    }

    void on();

    void off();
}
