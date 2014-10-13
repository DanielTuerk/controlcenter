package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class BusConnectionToggleButton extends ToggleButton {

    public BusConnectionToggleButton(final DeviceListBox deviceListBox) {
        super(new Image("img/icon/power_off.png"),
                new Image("img/icon/power_on.png"));

        ensureDebugId("cwCustomButton-toggle-normal");
        setPixelSize(25, 25);
        addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    ServiceUtils.getBusService().changeDevice(deviceListBox.getSelectedDevice(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                        }

                        @Override
                        public void onSuccess(Void result) {
                            ServiceUtils.getBusService().connectBus(new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    setValue(false);
                                }

                                @Override
                                public void onSuccess(Void result) {
                                    setValue(true);
                                    // TODO should be an event from server
                                    BusConnection.getInstance().setConnected();
                                }
                            });
                        }
                    });
                } else {
                    ServiceUtils.getBusService().disconnectBus(new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            setValue(true);
                        }

                        @Override
                        public void onSuccess(Void result) {
                            setValue(false);
                            BusConnection.getInstance().setDisconnected();
                        }
                    });
                }
            }
        });
    }
}
