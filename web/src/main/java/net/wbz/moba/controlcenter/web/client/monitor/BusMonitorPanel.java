package net.wbz.moba.controlcenter.web.client.monitor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteConnectionListener;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import org.gwtbootstrap3.client.ui.Well;

/**
 * The BusMonitor shows the current data of each address for bus 0 and 1. The monitor appears when it`s connected to the
 * bus otherwise an information will be shown.
 *
 * @author Daniel Tuerk
 */
public class BusMonitorPanel extends Composite {

    private static Binder uiBinder = GWT.create(Binder.class);

    private final BusMonitorContainer busMonitorContainer;
    private final RemoteConnectionListener connectionListener;

    @UiField
    Well wellConnectionState;
    @UiField
    HTMLPanel panel;

    /**
     * Create monitor panel.
     */
    public BusMonitorPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        busMonitorContainer = new BusMonitorContainer();
        connectionListener = new RemoteConnectionListener() {
            @Override
            public void connected(DeviceInfo deviceInfoEvent) {
                addMonitor();
            }

            @Override
            public void disconnected(DeviceInfo deviceInfoEvent) {
                removeMonitor();
            }
        };
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(connectionListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(connectionListener);
        removeMonitor();
    }

    private void removeMonitor() {
        panel.remove(busMonitorContainer);
        panel.add(wellConnectionState);
    }

    private void addMonitor() {
        panel.remove(wellConnectionState);
        panel.add(busMonitorContainer);
    }

    interface Binder extends UiBinder<Widget, BusMonitorPanel> {

    }
}
