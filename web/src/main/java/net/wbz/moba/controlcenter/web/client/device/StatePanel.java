package net.wbz.moba.controlcenter.web.client.device;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.client.util.OnOffToggleButton;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.PlayerEvent;
import net.wbz.moba.controlcenter.web.shared.bus.RecordingEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.RailVoltageEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Panel for the state of the bus.
 * Configure and connect devices.
 *
 * @author Daniel Tuerk
 */
public class StatePanel extends FlowPanel {

    private final RemoteEventListener deviceInfoEventListener;
    private final SendDataModal sendDataModal = new SendDataModal();
    private final Button btnSendData;
    private final DeviceListBox deviceListBox;
    private final RecordingModal recordingModal = new RecordingModal();
    private final ToggleSwitch toggleRecording;
    private final Button btnPlayerStart = new Button("Play");
    private final Button btnPlayerStop = new Button("Stop");
    private final PlayerModal playerModal = new PlayerModal();
    private final RemoteEventListener busDataPlayerEventListener;
    private final RemoteEventListener voltageEventListener;
    private final RemoteEventListener recordingEventListener;
    private ToggleSwitch toggleRailVoltage;
    private Button btnDeviceConfig;
    private BusConnectionToggleButton busConnectionToggleButton;

    public StatePanel() {
        setStyleName("statePanel");

        // add event receiver for the device connection state
        deviceInfoEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof DeviceInfoEvent) {
                    DeviceInfoEvent event = (DeviceInfoEvent) anEvent;
                    if (event.getEventType() == DeviceInfoEvent.TYPE.CONNECTED) {
                        updateDeviceConnectionState(event.getDeviceInfo(), true);
                    } else if (event.getEventType() == DeviceInfoEvent.TYPE.DISCONNECTED) {
                        updateDeviceConnectionState(event.getDeviceInfo(), false);
                    }
                }
            }
        };

        // add event receiver for the device connection state
        busDataPlayerEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof PlayerEvent) {
                    PlayerEvent event = (PlayerEvent) anEvent;
                    updatePlayerState(event.getState() == PlayerEvent.STATE.START);

                    Notify.notify("Player " + event.getState().name() + "!");
                }
            }
        };

        deviceListBox = new DeviceListBox();
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
        toggleRailVoltage = new OnOffToggleButton("Voltage", new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                toggleRailVoltageState();
            }
        });
        voltageEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof RailVoltageEvent) {
                    toggleRailVoltage.setValue(((RailVoltageEvent) anEvent).isState(), false);
                }
            }
        };

        add(toggleRailVoltage);

        btnSendData = new Button("send");
        btnSendData.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                sendDataModal.show();
            }
        });
        add(btnSendData);

        toggleRecording = new OnOffToggleButton("Record", new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                if (booleanValueChangeEvent.getValue()) {
                    RequestUtils.getInstance().getBusService().startRecording("", RequestUtils.VOID_ASYNC_CALLBACK);
                } else {
                    RequestUtils.getInstance().getBusService().stopRecording(RequestUtils.VOID_ASYNC_CALLBACK);
                }
            }
        });
        recordingEventListener = new RemoteEventListener() {
            public void apply(Event anEvent) {
                if (anEvent instanceof RecordingEvent) {
                    RecordingEvent recordingEvent = (RecordingEvent) anEvent;
                    if (recordingEvent.getState() == RecordingEvent.STATE.STOP) {
                        toggleRecording.setValue(false, false);
                        recordingModal.show(recordingEvent);
                    }
                }
            }
        };

        add(toggleRecording);

        // player
        btnPlayerStart.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                playerModal.show();
            }
        });
        add(btnPlayerStart);

        btnPlayerStop.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RequestUtils.getInstance().getBusService().stopRecording(RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
        add(btnPlayerStop);

        add(new Label("v0.01.alpha"));

    }

    private void updatePlayerState(boolean playing) {
        btnPlayerStart.setEnabled(!playing);
        btnPlayerStop.setEnabled(playing);
    }

    private void toggleRailVoltageState() {
        RequestUtils.getInstance().getBusService().toggleRailVoltage(RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @Override
    protected void onLoad() {
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceInfoEventListener);
        EventReceiver.getInstance().addListener(PlayerEvent.class, busDataPlayerEventListener);
        EventReceiver.getInstance().addListener(RailVoltageEvent.class, voltageEventListener);
        EventReceiver.getInstance().addListener(RecordingEvent.class, recordingEventListener);

        RequestUtils.getInstance().getBusService().isBusConnected(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Boolean connected) {
                if (connected) {
                    RequestUtils.getInstance().getBusService().getDevices(new AsyncCallback<Collection<DeviceInfo>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                        }

                        @Override
                        public void onSuccess(Collection<DeviceInfo> result) {
                            for (DeviceInfo deviceInfo : result) {
                                if (deviceInfo.isConnected()) {
                                    EventReceiver.getInstance().fireEvent(new DeviceInfoEvent(deviceInfo, DeviceInfoEvent.TYPE.CONNECTED));
                                    break;
                                }
                            }
                        }
                    });

                } else {
                    EventReceiver.getInstance().fireEvent(new DeviceInfoEvent(null, DeviceInfoEvent.TYPE.DISCONNECTED));
                }
            }
        });
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceInfoEventListener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, busDataPlayerEventListener);
        EventReceiver.getInstance().removeListener(RailVoltageEvent.class, voltageEventListener);
        EventReceiver.getInstance().removeListener(RecordingEvent.class, recordingEventListener);
    }

    private void updateDeviceConnectionState(@Nullable DeviceInfo deviceInfo, boolean connected) {
        toggleRailVoltage.setEnabled(connected);
        deviceListBox.setConnectedDevice(deviceInfo != null && deviceInfo.isConnected() ? deviceInfo : null);
        // TODO non visual feedback if disabled
        deviceListBox.setEnabled(!connected);
        btnDeviceConfig.setEnabled(!connected);
        busConnectionToggleButton.updateValue(connected);
        btnSendData.setEnabled(connected);
        toggleRecording.setEnabled(connected);

        // update data player state
        btnPlayerStart.setEnabled(connected);
        btnPlayerStop.setEnabled(connected);
        if (connected) {
            updatePlayerState(false);
        }
    }
}
