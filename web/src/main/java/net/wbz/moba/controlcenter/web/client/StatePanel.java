package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import javax.annotation.Nullable;
import net.wbz.moba.controlcenter.web.client.device.BusConnectionToggleButton;
import net.wbz.moba.controlcenter.web.client.device.DeviceListBox;
import net.wbz.moba.controlcenter.web.client.device.PlayerModal;
import net.wbz.moba.controlcenter.web.client.device.RecordingModal;
import net.wbz.moba.controlcenter.web.client.device.SendDataModal;
import net.wbz.moba.controlcenter.web.client.device.config.DeviceConfigModal;
import net.wbz.moba.controlcenter.web.client.event.EventReceiver;
import net.wbz.moba.controlcenter.web.client.event.bus.BusPlayerRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.bus.BusRecordingRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.device.RailVoltageRemoteListener;
import net.wbz.moba.controlcenter.web.client.event.device.RemoteConnectionListener;
import net.wbz.moba.controlcenter.web.client.request.RequestUtils;
import net.wbz.moba.controlcenter.web.shared.bus.DeviceInfo;
import net.wbz.moba.controlcenter.web.shared.bus.PlayerEvent;
import net.wbz.moba.controlcenter.web.shared.bus.RecordingEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

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
    private final RemoteConnectionListener deviceInfoEventListener;
    private final BusPlayerRemoteListener busDataPlayerEventListener;
    private final RailVoltageRemoteListener voltageEventListener;
    private final BusRecordingRemoteListener recordingEventListener;
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

        deviceInfoEventListener = new RemoteConnectionListener() {
            @Override
            public void connected(DeviceInfo deviceInfoEvent) {
                updateDeviceConnectionState(deviceInfoEvent, true);
            }

            @Override
            public void disconnected(DeviceInfo deviceInfoEvent) {
                updateDeviceConnectionState(deviceInfoEvent, false);
            }
        };
        // add event receiver for the device connection state
        busDataPlayerEventListener = new BusPlayerRemoteListener() {
            @Override
            public void start(PlayerEvent event) {
                updatePlayerState(true);
                Notify.notify("Player started!");
            }

            @Override
            public void stop(PlayerEvent event) {
                updatePlayerState(false);
                Notify.notify("Player stopped!");
            }
        };

        busConnectionToggleButton.addValueChangeHandler(deviceListBox);
        toggleRailVoltage.addValueChangeHandler(booleanValueChangeEvent -> toggleRailVoltageState());
        voltageEventListener = new RailVoltageRemoteListener() {
            @Override
            public void on() {
                toggleRailVoltage.setValue(true, false);
            }

            @Override
            public void off() {
                toggleRailVoltage.setValue(false, false);
            }
        };
        toggleRecording.addValueChangeHandler(booleanValueChangeEvent -> {
            if (booleanValueChangeEvent.getValue()) {
                RequestUtils.getInstance().getBusService().startRecording("", RequestUtils.VOID_ASYNC_CALLBACK);
            } else {
                RequestUtils.getInstance().getBusService().stopRecording(RequestUtils.VOID_ASYNC_CALLBACK);
            }
        });
        recordingEventListener = new BusRecordingRemoteListener() {
            @Override
            public void start(RecordingEvent event) {
            }

            @Override
            public void stop(RecordingEvent event) {
                toggleRecording.setValue(false, false);
                recordingModal.show(event);
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
        EventReceiver.getInstance()
            .addListener(deviceInfoEventListener, busDataPlayerEventListener, voltageEventListener,
                recordingEventListener);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        EventReceiver.getInstance()
            .removeListener(deviceInfoEventListener, busDataPlayerEventListener, voltageEventListener,
                recordingEventListener);
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
        // TODO non visual feedback if disabled
        deviceListBox.setEnabled(!connected);
        deviceListBox.setSelectedDevice(deviceInfo != null && deviceInfo.isConnected() ? deviceInfo : null);
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
