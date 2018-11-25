package net.wbz.moba.controlcenter.web.client.device;

import javax.annotation.Nullable;

import net.wbz.moba.controlcenter.web.client.device.config.DeviceConfigModal;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;
import net.wbz.moba.controlcenter.web.client.EventReceiver;
import net.wbz.moba.controlcenter.web.client.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfoEvent;
import net.wbz.moba.controlcenter.web.shared.bus.PlayerEvent;
import net.wbz.moba.controlcenter.web.shared.bus.RecordingEvent;
import net.wbz.moba.controlcenter.web.shared.viewer.RailVoltageEvent;

/**
 * Panel for the state of the bus.
 * Configure and connect devices.
 *
 * @author Daniel Tuerk
 */
public class StatePanel extends Composite {

    private static StatePanelBinder uiBinder = GWT.create(StatePanelBinder.class);
    private final SendDataModal sendDataModal = new SendDataModal();
    private final RecordingModal recordingModal = new RecordingModal();
    private final PlayerModal playerModal = new PlayerModal();
    private final RemoteEventListener deviceInfoEventListener;
    private final RemoteEventListener busDataPlayerEventListener;
    private final RemoteEventListener voltageEventListener;
    private final RemoteEventListener recordingEventListener;
    @UiField
    Button btnSendData;
    @UiField
    DeviceListBox deviceListBox;
    @UiField
    ToggleSwitch toggleRecording;
    @UiField
    Button btnPlayerStart;
    @UiField
    Button btnPlayerStop;
    @UiField
    ToggleSwitch toggleRailVoltage;
    @UiField
    Button btnDeviceConfig;
    @UiField
    BusConnectionToggleButton busConnectionToggleButton;

    public StatePanel() {
        initWidget(uiBinder.createAndBindUi(this));

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

        busConnectionToggleButton.addValueChangeHandler(deviceListBox);
        toggleRailVoltage.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
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
        toggleRecording.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
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
    }

    @UiHandler("btnDeviceConfig")
    void onClickDeviceConfig(ClickEvent ignored) {
        new DeviceConfigModal().show();
    }

    @UiHandler("btnSendData")
    void onClickSendData(ClickEvent ignored) {
        sendDataModal.show();
    }

    @UiHandler("btnPlayerStart")
    void onClickPlayerStart(ClickEvent ignored) {
        playerModal.show();
    }

    @UiHandler("btnPlayerStop")
    void onClickPlayerStop(ClickEvent ignored) {
        RequestUtils.getInstance().getBusService().stopRecording(RequestUtils.VOID_ASYNC_CALLBACK);
    }

    @Override
    protected void onLoad() {
        EventReceiver.getInstance().addListener(DeviceInfoEvent.class, deviceInfoEventListener);
        EventReceiver.getInstance().addListener(PlayerEvent.class, busDataPlayerEventListener);
        EventReceiver.getInstance().addListener(RailVoltageEvent.class, voltageEventListener);
        EventReceiver.getInstance().addListener(RecordingEvent.class, recordingEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, deviceInfoEventListener);
        EventReceiver.getInstance().removeListener(DeviceInfoEvent.class, busDataPlayerEventListener);
        EventReceiver.getInstance().removeListener(RailVoltageEvent.class, voltageEventListener);
        EventReceiver.getInstance().removeListener(RecordingEvent.class, recordingEventListener);
    }

    private void updatePlayerState(boolean playing) {
        btnPlayerStart.setEnabled(!playing);
        btnPlayerStop.setEnabled(playing);
    }

    private void toggleRailVoltageState() {
        RequestUtils.getInstance().getBusService().toggleRailVoltage(RequestUtils.VOID_ASYNC_CALLBACK);
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

    interface StatePanelBinder extends UiBinder<Widget, StatePanel> {
    }
}
