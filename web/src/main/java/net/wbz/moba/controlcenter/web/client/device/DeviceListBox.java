package net.wbz.moba.controlcenter.web.client.device;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class DeviceListBox extends Select {

    private final List<DeviceInfo> devices = Lists.newArrayList();

    public DeviceListBox() {
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, new RemoteEventListener() {
            @Override
            public void apply(Event event) {
                reload();
            }
        });
        reload();
    }

    private DeviceInfo getDevice(String value) {
        for (DeviceInfo deviceInfo : devices) {
            if (deviceInfo.getKey().endsWith(value)) {
                return deviceInfo;
            }
        }
        return null;
    }

    public void reload() {

        ServiceUtils.getBusService().getDevices(new AsyncCallback<ArrayList<DeviceInfo>>() {
            @Override
            public void onFailure(Throwable caught) {
                setValue("error:can't load: " + caught.getMessage());
            }

            @Override
            public void onSuccess(ArrayList<DeviceInfo> result) {
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

    public List<DeviceInfo> getDevices() {
        return devices;
    }

    public DeviceInfo getSelectedDevice() {
        return getDevice(getValue());
    }

    public void setConnectedDevice(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            setValue(deviceInfo.getKey());
        }
    }
}
