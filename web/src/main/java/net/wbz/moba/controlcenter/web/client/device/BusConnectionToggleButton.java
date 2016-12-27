package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.ColorType;

/**
 * Toggle button to connect and disconnect from the bus for the selected device in
 * the {@link net.wbz.moba.controlcenter.web.client.device.DeviceListBox}.
 *
 * @author Daniel Tuerk
 */
public class BusConnectionToggleButton extends ToggleSwitch {

    /**
     * Quick fix {@see BusConnectionToggleButton#setValue}.
     */
    private boolean fireEvent = true;

    public BusConnectionToggleButton(final DeviceListBox deviceListBox) {
        super();
        setLabelText("Bus");
        setOffColor(ColorType.DANGER);
        setOnColor(ColorType.SUCCESS);
        addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                if (fireEvent) {
                    if (booleanValueChangeEvent.getValue()) {
                        RequestUtils.getInstance().getBusRequest().changeDevice(deviceListBox.getSelectedDevice(),
                                new AsyncCallback<Void>() {
                                    @Override
                                    public void onFailure(Throwable caught) {

                                    }

                                    @Override
                                    public void onSuccess(Void result) {
                                        RequestUtils.getInstance().getBusRequest().connectBus(RequestUtils.VOID_ASYNC_CALLBACK);

                                    }
                                });
                    } else {
                        RequestUtils.getInstance().getBusRequest().disconnectBus(RequestUtils.VOID_ASYNC_CALLBACK);
                    }
                } else {
                    // activate for next timestamp
                    fireEvent = true;
                }

            }
        });
    }

    /**
     * Quick fix for the fire events state. Bootstrap lib also fire the value change event if the
     * parameter is {@code false}.
     *
     * @param value      value to set
     * @param fireEvents do not fire events for value changed
     */
    public void updateValue(Boolean value, boolean fireEvents) {
        fireEvent = fireEvents;
        super.setValue(value, true);
    }
}
