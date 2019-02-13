package net.wbz.moba.controlcenter.web.client.viewer.track;

import net.wbz.moba.controlcenter.web.client.editor.track.AbstractTrackPanel;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.device.RailVoltageRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteConnectionListener;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;

/**
 * Abstract viewer panel for the track.
 * Initialize the events for state changes.
 * The implementation takes care about the presentation of the track.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractTrackViewerPanel extends AbstractTrackPanel {

    public static final String ID = "trackViewerPanel";

    public static final int PERCENTAGE_MAX = 99;
    public static final int PERCENTAGE_START_TRACK = 30;

    private final RemoteConnectionListener deviceConnectionEventListener;
    private final RailVoltageRemoteListener railVoltageEventListener;

    protected AbstractTrackViewerPanel() {
        super();
        deviceConnectionEventListener = new RemoteConnectionListener() {
            @Override
            public void connected(DeviceInfo deviceInfoEvent) {
                enableTrackWidgets();
            }

            @Override
            public void disconnected(DeviceInfo deviceInfoEvent) {
                disableTrackWidgets();
            }
        };
        railVoltageEventListener = new RailVoltageRemoteListener() {
            @Override
            public void on() {
                resetTrackForRailVoltage(true);
            }

            @Override
            public void off() {
                resetTrackForRailVoltage(false);
            }
        };
        getElement().setId(ID);
    }

    protected void resetTrackForRailVoltage(boolean railVoltageOn) {
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(deviceConnectionEventListener);
        EventReceiver.getInstance().addListener(railVoltageEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(deviceConnectionEventListener);
        EventReceiver.getInstance().removeListener(railVoltageEventListener);
    }

    protected void enableTrackWidgets() {
    }

    protected void disableTrackWidgets() {
    }
}
