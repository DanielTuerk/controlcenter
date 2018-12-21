package net.wbz.moba.controlcenter.web.client.viewer.track;

import net.wbz.moba.controlcenter.web.client.editor.track.AbstractTrackPanel;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.track.FeedbackTrackBlockRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.device.RailVoltageRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteConnectionListener;
import net.wbz.moba.controlcenter.web.client.event.track.SignalFunctionRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.track.TrackBlockRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.track.TrackPartStateRemoteListener;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent.STATE;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractTrackViewerPanel extends AbstractTrackPanel {

    public static final String ID = "trackViewerPanel";

    public static final int PERCENTAGE_MAX = 99;
    public static final int PERCENTAGE_START_TRACK = 30;

    private final TrackPartStateRemoteListener trackPartStateEventListener;
    private final SignalFunctionRemoteListener signalFunctionStateEventListener;
    private final RemoteConnectionListener deviceConnectionEventListener;
    private final TrackBlockRemoteListener blockEventListener;
    private final FeedbackTrackBlockRemoteListener feedbackBlockEventListener;
    private final RailVoltageRemoteListener railVoltageEventListener;

    protected AbstractTrackViewerPanel() {
        signalFunctionStateEventListener = this::updateSignalState;
        trackPartStateEventListener = event -> updateTrackPartState(event.getConfiguration(), event.isOn());
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
        blockEventListener = event -> updateTrackPartBlockState(event.getConfig(),
            event.getState() == STATE.USED);

        feedbackBlockEventListener = event -> updateTrainOnTrack(event.getAddress(), event.getBlock(), event.getTrain(),
            event.getState());
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

    protected void updateSignalState(SignalFunctionStateEvent signalFunctionStateEvent) {

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(trackPartStateEventListener);
        EventReceiver.getInstance().addListener(signalFunctionStateEventListener);
        EventReceiver.getInstance().addListener(deviceConnectionEventListener);
        EventReceiver.getInstance().addListener(blockEventListener);
        EventReceiver.getInstance().addListener(feedbackBlockEventListener);
        EventReceiver.getInstance().addListener(railVoltageEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(trackPartStateEventListener);
        EventReceiver.getInstance().removeListener(signalFunctionStateEventListener);
        EventReceiver.getInstance().removeListener(deviceConnectionEventListener);
        EventReceiver.getInstance().removeListener(blockEventListener);
        EventReceiver.getInstance().removeListener(feedbackBlockEventListener);
        EventReceiver.getInstance().removeListener(railVoltageEventListener);
    }

    protected void updateTrackPartState(BusDataConfiguration configuration, boolean state) {

    }

    protected abstract void updateTrackPartBlockState(BusDataConfiguration configuration, boolean state);

    /**
     * Show train label on the given block.
     *
     * @param address address of the block
     * @param block number of the block
     * @param train address of the train
     * @param state {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent.STATE} enter or exit the block
     */
    protected void updateTrainOnTrack(final int address, final int block, final int train,
        final FeedbackBlockEvent.STATE state) {

    }

    protected void enableTrackWidgets() {

    }

    protected void disableTrackWidgets() {

    }
}
