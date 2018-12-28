package net.wbz.moba.controlcenter.web.client.viewer.controls;

import com.google.gwt.user.client.ui.Composite;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteConnectionListener;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * Abstract container composite to handle the device connection state for each viewer controls item.
 *
 * @author Daniel Tuerk
 */
abstract public class AbstractViewerItemControlsComposite<Model extends AbstractDto> extends Composite {

    private final RemoteConnectionListener remoteConnectionListener;
    private final Model model;

    public AbstractViewerItemControlsComposite(Model model) {
        this.model = model;

        remoteConnectionListener = new RemoteConnectionListener() {
            @Override
            public void connected(DeviceInfo deviceInfoEvent) {
                deviceConnectionChanged(true);
            }

            @Override
            public void disconnected(DeviceInfo deviceInfoEvent) {
                deviceConnectionChanged(false);
            }

        };
    }

    protected abstract void deviceConnectionChanged(boolean connected);

    @Override
    protected void onLoad() {
        super.onLoad();
        EventReceiver.getInstance().addListener(remoteConnectionListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(remoteConnectionListener);
    }

    public Model getModel() {
        return model;
    }

}
