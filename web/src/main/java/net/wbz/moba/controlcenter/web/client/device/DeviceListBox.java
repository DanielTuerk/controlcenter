package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteDeviceListener;
import net.wbz.moba.controlcenter.web.client.request.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;

/**
 * Select for the {@link DeviceInfo}.
 *
 * @author Daniel Tuerk
 */
public class DeviceListBox extends Composite {

    private static Binder UI_BINDER = GWT.create(Binder.class);
    private final List<DeviceInfo> devices = new ArrayList<>();
    private final RemoteDeviceListener remoteDeviceListener;
    @UiField
    Button btnDropup;
    @UiField
    DropDownMenu dropDownMenu;

    public DeviceListBox() {
        initWidget(UI_BINDER.createAndBindUi(this));

        remoteDeviceListener = new RemoteDeviceListener() {
            @Override
            public void devicesModified(DeviceInfo deviceInfo) {
                reload();
            }

            @Override
            public void deviceCreated(DeviceInfo deviceInfoEvent) {
                reload();
            }

            @Override
            public void deviceRemoved(DeviceInfo deviceInfoEvent) {
                reload();
            }
        };
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        EventReceiver.getInstance().addListener(remoteDeviceListener);

        reload();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(remoteDeviceListener);
    }


    DeviceInfo getSelectedDevice() {
        return getDevice(btnDropup.getText());
    }

    public void setSelectedDevice(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            DeviceInfo device = getDevice(deviceInfo.getKey());
            if (device != null) {
                btnDropup.setText(device.getKey());
            }
        }
    }


    public void setEnabled(boolean b) {
        btnDropup.setEnabled(b);
    }

    private void reload() {
        RequestUtils.getInstance().getBusService().getDevices(new OnlySuccessAsyncCallback<Collection<DeviceInfo>>() {
            @Override
            public void onSuccess(Collection<DeviceInfo> result) {
                devices.clear();
                devices.addAll(result);

                // re-create item list
                dropDownMenu.clear();
                for (DeviceInfo device : result) {
                    String deviceKey = device.getKey();
                    AnchorListItem child = new AnchorListItem(deviceKey);
                    child.addClickHandler(clickEvent -> btnDropup.setText(deviceKey));
                    dropDownMenu.add(child);
                }
                // select first as default
                if (!result.isEmpty()) {
                    setSelectedDevice(result.iterator().next());
                }
            }
        });
    }

    private DeviceInfo getDevice(String value) {
        for (DeviceInfo deviceInfo : devices) {
            if (deviceInfo.getKey().endsWith(value)) {
                return deviceInfo;
            }
        }
        return null;
    }

    interface Binder extends UiBinder<Widget, DeviceListBox> {

    }
}

