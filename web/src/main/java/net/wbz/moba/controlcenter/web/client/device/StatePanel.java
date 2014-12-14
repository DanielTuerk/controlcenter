package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.ServiceUtils;
import net.wbz.moba.controlcenter.web.client.util.EmptyCallback;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.RailVoltageEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.ColorType;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
public class StatePanel extends org.gwtbootstrap3.client.ui.gwt.FlowPanel {

    private final RemoteEventListener deviceInfoEventListener;
    private ToggleSwitch toggleRailVoltage;
    private Button btnDeviceConfig;
    private BusConnectionToggleButton busConnectionToggleButton;
    private final SendDataModal sendDataModal = new SendDataModal();
    private final Button btnSendData;

    public StatePanel() {
        setStyleName("statePanel");

        // add event receiver for the device connection state
        deviceInfoEventListener = new RemoteEventListener() {
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
        };

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
        toggleRailVoltage = new ToggleSwitch();
        toggleRailVoltage.setOffColor(ColorType.DANGER);
        toggleRailVoltage.setOnColor(ColorType.SUCCESS);
        toggleRailVoltage.setLabelText("Voltage");
        toggleRailVoltage.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                toggleRailVoltageState();
            }
        });

        EventReceiver.getInstance().addListener(RailVoltageEvent.class, new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof RailVoltageEvent) {
                    toggleRailVoltage.setValue(((RailVoltageEvent) anEvent).isState(), false);
                }
            }
        });

        add(toggleRailVoltage);
        add(new Label("v0.01.alpha"));

        btnSendData = new Button("send");
        btnSendData.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                sendDataModal.show();
            }
        });
        add(btnSendData);
    }

    private void toggleRailVoltageState() {
        ServiceUtils.getBusService().toggleRailVoltage(new EmptyCallback<Void>());
    }

    @Override
    protected void onLoad() {
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceInfoEventListener);

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

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceInfoEventListener);
    }

    private void updateDeviceConnectionState(boolean connected) {
        toggleRailVoltage.setEnabled(connected);
        btnDeviceConfig.setEnabled(!connected);
        busConnectionToggleButton.setValue(connected, false);
        btnSendData.setEnabled(connected);
    }
}
