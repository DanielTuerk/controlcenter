package net.wbz.moba.controlcenter.web.client.device;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.client.widgets.ToggleButton;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.RailVoltageEvent;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class StatePanel extends FlowPanel {

    private ToggleButton toggleRailVoltage;
    private Button btnDeviceConfig;
    private BusConnectionToggleButton busConnectionToggleButton;

    public StatePanel() {

        setStyleName("statePanel");

        // add event receiver for the device connection state
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof DeviceInfoEvent) {
                    DeviceInfoEvent event = (DeviceInfoEvent) anEvent;
                    if (event.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
                        updateDeviceConnectionState(true);
                    } else if (event.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                        updateDeviceConnectionState(false);
                    }
                }
            }
        });

        DeviceListBox deviceListBox = new DeviceListBox();
        busConnectionToggleButton = new BusConnectionToggleButton(deviceListBox);
        add(busConnectionToggleButton);
        add(deviceListBox);

        final DeviceConfigModal configureDeviceModal = new DeviceConfigModal();

        btnDeviceConfig = new Button("Config");
        btnDeviceConfig.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                configureDeviceModal.show();
            }
        });
        add(btnDeviceConfig);

        add(new Label("SX-Bus"));
        toggleRailVoltage = new ToggleButton("Rail Voltage");
        toggleRailVoltage.addToggleHandler(new ToggleButton.ToggleHandler() {
            @Override
            public void isOn() {
                toggleRailVoltageState();
            }

            @Override
            public void isOff() {
                toggleRailVoltageState();
            }
        });

        EventReceiver.getInstance().addListener(RailVoltageEvent.class, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof RailVoltageEvent) {
                    toggleRailVoltage.setActive(((RailVoltageEvent) anEvent).isState());
                }
            }
        });

        add(toggleRailVoltage);
        add(new Label("v0.01.alpha"));

    }

    private void toggleRailVoltageState() {
        ServiceUtils.getBusService().toggleRailVoltage(new EmptyCallback<Void>());
    }

    @Override
    protected void onLoad() {
        ServiceUtils.getBusService().isBusConnected(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                updateDeviceConnectionState(aBoolean);
            }
        });
    }

    private void updateDeviceConnectionState(boolean connected) {
        toggleRailVoltage.setEnabled(connected);
        btnDeviceConfig.setEnabled(!connected);
        busConnectionToggleButton.setActive(connected);
    }
}
