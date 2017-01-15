package net.wbz.moba.controlcenter.web.client.viewer.track;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.editor.track.AbstractTrackPanel;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;
import net.wbz.moba.controlcenter.web.shared.viewer.SignalFunctionStateEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartBlockEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.TrackPartStateEvent;

/**
 * @author Daniel Tuerk
 */
abstract public class AbstractTrackViewerPanel extends AbstractTrackPanel {

    public static final String ID = "trackViewerPanel";

    public static final int PERCENTAGE_MAX = 99;
    public static final int PERCENTAGE_START_TRACK = 30;

    private final RemoteEventListener trackPartStateEventListener;
    private final RemoteEventListener signalFunctionStateEventListener;
    private final RemoteEventListener deviceConnectionEventListener;
    private final RemoteEventListener blockEventListener;
    private final RemoteEventListener feedbackBlockEventListener;

    public AbstractTrackViewerPanel() {
        signalFunctionStateEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof SignalFunctionStateEvent) {
                    updateSignalState((SignalFunctionStateEvent) anEvent);
                }
            }
        };
        trackPartStateEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof TrackPartStateEvent) {
                    TrackPartStateEvent event = (TrackPartStateEvent) anEvent;
                    updateTrackPartState(event.getConfiguration(), event.isOn());
                }
            }
        };
        deviceConnectionEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof DeviceInfoEvent) {
                    DeviceInfoEvent event = (DeviceInfoEvent) anEvent;
                    if (event.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
                        enableTrackWidgets();
                    } else if (event.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                        disableTrackWidgets();
                    }
                }
            }
        };
        blockEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof TrackPartBlockEvent) {
                    TrackPartBlockEvent event = (TrackPartBlockEvent) anEvent;
                    updateTrackPartBlockState(event.getConfig(),event.getState()== TrackPartBlockEvent.STATE.USED);
                }
            }
        };

        feedbackBlockEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof FeedbackBlockEvent) {
                    FeedbackBlockEvent event = (FeedbackBlockEvent) anEvent;
                    updateTrainOnTrack(event.getAddress(), event.getBlock(), event.getTrain(), event.getState());
                }
            }
        };
        getElement().setId(ID);
    }

    protected void updateSignalState(SignalFunctionStateEvent signalFunctionStateEvent) {

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(TrackPartStateEvent.class, trackPartStateEventListener);
        EventReceiver.getInstance().addListener(SignalFunctionStateEvent.class, signalFunctionStateEventListener);
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceConnectionEventListener);
        EventReceiver.getInstance().addListener(TrackPartBlockEvent.class, blockEventListener);
        EventReceiver.getInstance().addListener(FeedbackBlockEvent.class, feedbackBlockEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(TrackPartStateEvent.class, trackPartStateEventListener);
        EventReceiver.getInstance().removeListener(SignalFunctionStateEvent.class, signalFunctionStateEventListener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceConnectionEventListener);
        EventReceiver.getInstance().removeListener(TrackPartBlockEvent.class, blockEventListener);
        EventReceiver.getInstance().removeListener(FeedbackBlockEvent.class, feedbackBlockEventListener);
    }

    protected void updateTrackPartState(BusDataConfiguration configuration, boolean state) {

    }

    protected abstract void updateTrackPartBlockState(BusDataConfiguration configuration, boolean state);

    /**
     * Show train label on the given block.
     *
     * @param address address of the block
     * @param block   number of the block
     * @param train   address of the train
     * @param state   {@link net.wbz.moba.controlcenter.web.shared.bus.FeedbackBlockEvent.STATE} enter or exit the block
     */
    protected void updateTrainOnTrack(final int address, final int block, final int train, final FeedbackBlockEvent.STATE state) {

    }

    protected void enableTrackWidgets() {

    }

    protected void disableTrackWidgets() {

    }
}
