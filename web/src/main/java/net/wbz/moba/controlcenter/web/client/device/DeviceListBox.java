package net.wbz.moba.controlcenter.web.client.device;

import java.util.Collection;
import java.util.List;

import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoProxy;

import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.common.collect.Lists;
import com.google.web.bindery.requestfactory.shared.Receiver;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * @author Daniel Tuerk
 */
public class DeviceListBox extends Select {

    private final List<DeviceInfoProxy> devices = Lists.newArrayList();

    public DeviceListBox() {
        setWidth("180px");

        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, new RemoteEventListener() {
            @Override
            public void apply(Event event) {
                reload();
            }
        });
        reload();
    }

    private DeviceInfoProxy getDevice(String value) {
        for (DeviceInfoProxy deviceInfo : devices) {
            if (deviceInfo.getKey().endsWith(value)) {
                return deviceInfo;
            }
        }
        return null;
    }

    public void reload() {

        RequestUtils.getInstance().getBusRequest().getDevices().fire(new Receiver<Collection<DeviceInfoProxy>>() {
            @Override
            public void onSuccess(Collection<DeviceInfoProxy> result) {
                devices.clear();
                devices.addAll(result);

                DeviceListBox.this.clear();
                for (DeviceInfoProxy device : result) {
                    Option child = new Option();
                    child.setValue(device.getKey());
                    child.setText(device.getKey());
                    add(child);
                }
                DeviceListBox.this.refresh();
            }
        });
    }

    public List<DeviceInfoProxy> getDevices() {
        return devices;
    }

    public DeviceInfoProxy getSelectedDevice() {
        return getDevice(getValue());
    }

    public void setConnectedDevice(DeviceInfoProxy deviceInfo) {
        if (deviceInfo != null) {
            setValue(deviceInfo.getKey());
        }
    }
}
