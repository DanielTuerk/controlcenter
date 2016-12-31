package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.OnOffToggleButton;

/**
 * Toggle button to connect and disconnect from the bus for the selected device in
 * the {@link net.wbz.moba.controlcenter.web.client.device.DeviceListBox}.
 *
 * @author Daniel Tuerk
 */
public class BusConnectionToggleButton extends OnOffToggleButton {

    public BusConnectionToggleButton(final DeviceListBox deviceListBox) {
        super("Bus", new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                if (booleanValueChangeEvent.getValue()) {
                    RequestUtils.getInstance().getBusService().changeDevice(deviceListBox.getSelectedDevice(),
                            new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {

                                }

                                @Override
                                public void onSuccess(Void result) {
                                    RequestUtils.getInstance().getBusService().connectBus(RequestUtils.VOID_ASYNC_CALLBACK);

                                }
                            });
                } else {
                    RequestUtils.getInstance().getBusService().disconnectBus(RequestUtils.VOID_ASYNC_CALLBACK);
                }

            }
        });
    }

}
