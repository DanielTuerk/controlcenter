package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.util.Log;
import net.wbz.moba.controlcenter.web.client.widgets.ToggleButton;

/**
 * Toggle button to connect and disconnect from the bus for the selected device in
 * the {@link net.wbz.moba.controlcenter.web.client.device.DeviceListBox}.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusConnectionToggleButton extends ToggleButton {

    public BusConnectionToggleButton(final DeviceListBox deviceListBox) {
        super("connect", "disconnect");

//        super(new Image("img/icon/power_off.png"),
//                new Image("img/icon/power_on.png"));

        addToggleHandler(new ToggleHandler() {
            @Override
            public void isOn() {
                ServiceUtils.getBusService().changeDevice(deviceListBox.getSelectedDevice(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error("change device "+caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result) {
                        ServiceUtils.getBusService().connectBus(new EmptyCallback<Void>());
                    }
                });
            }

            @Override
            public void isOff() {
                ServiceUtils.getBusService().disconnectBus(new EmptyCallback<Void>());
            }
        });
    }
}
