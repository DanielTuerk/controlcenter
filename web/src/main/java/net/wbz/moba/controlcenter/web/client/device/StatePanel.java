package net.wbz.moba.controlcenter.web.client.device;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.shared.viewer.RailVoltageEvent;

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

        final DeviceConfigModal configureDeviceModal = new DeviceConfigModal();

        Button configButton = new Button("Config");
        configButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
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


         /* Logic for GWTEventService starts here */
        //add a listener to the SERVER_MESSAGE_DOMAIN
        EventReceiver.getInstance().addListener(RailVoltageEvent.class, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof RailVoltageEvent) {
                    toggleRailVoltage.setValue(((RailVoltageEvent) anEvent).isState(), false);
                }
            }
        });

    }

    @Override
    protected void onLoad() {
    }
}
