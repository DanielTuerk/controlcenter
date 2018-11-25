package net.wbz.moba.controlcenter.web.client.device;

import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

/**
 * Select for the {@link DeviceInfo}.
 *
 * @author Daniel Tuerk
 */
class DeviceListBox extends Select {

    private final List<DeviceInfo> devices = new ArrayList<>();
    private final RemoteEventListener remoteEventListener = event -> reload();

    DeviceListBox() {
        setWidth("180px");
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, remoteEventListener);

        reload();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, remoteEventListener);
    }

    private DeviceInfo getDevice(String value) {
        for (DeviceInfo deviceInfo : devices) {
            if (deviceInfo.getKey().endsWith(value)) {
                return deviceInfo;
            }
        }
        return null;
    }

    private void reload() {
        RequestUtils.getInstance().getBusService().getDevices(new OnlySuccessAsyncCallback<Collection<DeviceInfo>>() {
            @Override
            public void onSuccess(Collection<DeviceInfo> result) {
                devices.clear();
                devices.addAll(result);

                DeviceListBox.this.clear();
                for (DeviceInfo device : result) {
                    Option child = new Option();
                    child.setValue(device.getKey());
                    child.setText(device.getKey());
                    add(child);
                }
                DeviceListBox.this.refresh();
            }
        });
    }

    DeviceInfo getSelectedDevice() {
        return getDevice(getValue());
    }

    void setConnectedDevice(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            setValue(deviceInfo.getKey(), false);
            refresh();
        }
    }
}
