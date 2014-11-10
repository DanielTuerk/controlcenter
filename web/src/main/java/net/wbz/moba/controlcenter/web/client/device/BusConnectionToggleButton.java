package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.util.Log;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.ColorType;

/**
 * Toggle button to connect and disconnect from the bus for the selected device in
 * the {@link net.wbz.moba.controlcenter.web.client.device.DeviceListBox}.
 *
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusConnectionToggleButton extends ToggleSwitch {

    public BusConnectionToggleButton(final DeviceListBox deviceListBox) {
        super();
        setLabelText("Bus");
        setOffColor(ColorType.DANGER);
        setOnColor(ColorType.SUCCESS);
//        setOnIcon(IconType.FLASH);
//        setOffIcon(IconType.FLASH);
        addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                if (booleanValueChangeEvent.getValue()) {
                    ServiceUtils.getBusService().changeDevice(deviceListBox.getSelectedDevice(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error("change device " + caught.getMessage());
                        }

                        @Override
                        public void onSuccess(Void result) {
                            ServiceUtils.getBusService().connectBus(new EmptyCallback<Void>());
                        }
                    });
                } else {
                    {
                        ServiceUtils.getBusService().disconnectBus(new EmptyCallback<Void>());
                    }
                }
            }
        });
    }
}
