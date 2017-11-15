package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import net.wbz.moba.controlcenter.web.client.Callbacks.OnlySuccessAsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.components.OnOffToggleButton;

/**
 * Toggle button to connect and disconnect from the bus for the selected device in
 * the {@link net.wbz.moba.controlcenter.web.client.device.DeviceListBox}.
 *
 * @author Daniel Tuerk
 */
public class BusConnectionToggleButton extends OnOffToggleButton {

    public BusConnectionToggleButton() {
        super();
        setLabelText("Bus");
    }

    public void addValueChangeHandler(final DeviceListBox deviceListBox) {
        addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                if (booleanValueChangeEvent.getValue()) {
                    RequestUtils.getInstance().getBusService().changeDevice(deviceListBox.getSelectedDevice(),
                            new OnlySuccessAsyncCallback<Void>("Editor", "error connect device") {
                                @Override
                                public void onSuccess(Void result) {
                                    RequestUtils.getInstance().getBusService().connectBus(
                                            RequestUtils.VOID_ASYNC_CALLBACK);
                                }
                            });
                } else {
                    RequestUtils.getInstance().getBusService().disconnectBus(RequestUtils.VOID_ASYNC_CALLBACK);
                }
            }
        });
    }
}
