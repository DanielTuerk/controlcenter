package net.wbz.moba.controlcenter.web.client.device;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class StatePanel extends HorizontalPanel {

    private final DeviceListBox deviceListBox = new DeviceListBox();
    private final BusConnectionToggleButton busConnectionToggleButton = new BusConnectionToggleButton(deviceListBox);

    public StatePanel() {
        setStyleName("statePanel");
        setSpacing(10);

        add(busConnectionToggleButton);
        add(deviceListBox);

        final DeviceConfigModal configureDeviceModal  = new DeviceConfigModal(deviceListBox);

        Button configButton = new Button("Config");
        configButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
//                configureDeviceModal.center();
                configureDeviceModal.show();
            }
        });
        add(configButton);

        add(new Label("SX-Bus"));
        final ToggleButton toggleRailVoltage = new ToggleButton("Rail Voltage");
        //TODO: event for state + by connected
        toggleRailVoltage.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                ServiceUtils.getBusService().toggleRailVoltage(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(Void result) {

                    }
                });
            }
        });
        add(toggleRailVoltage);
        add(new Label("v0.01.alpha"));
    }

    @Override
    protected void onLoad() {
    }
}
