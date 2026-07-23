package io.github.danieltuerk.controlcenter.service.bus;

import io.github.danieltuerk.controlcenter.EventBroadcaster;
import io.github.danieltuerk.controlcenter.Workspace;
import io.github.danieltuerk.controlcenter.shared.bus.RecordingEvent;
import io.github.danieltuerk.selectrix4java.data.recording.IsRecordable;
import io.github.danieltuerk.selectrix4java.device.Device;
import io.github.danieltuerk.selectrix4java.device.DeviceAccessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class DeviceRecorder {

    private final EventBroadcaster eventBroadcaster;
    @Getter
    private final Path destinationFolder;

    private IsRecordable recordable;

    @Inject
    public DeviceRecorder(EventBroadcaster eventBroadcaster, Workspace workspace) {
        this.eventBroadcaster = eventBroadcaster;
        this.destinationFolder = workspace.getRecordFolder();
    }

    public void startRecording(Device device) {
        if (device instanceof IsRecordable) {
            this.recordable = (IsRecordable) device;
        } else {
            throw new RuntimeException("device is no instance of " + IsRecordable.class.getName());
        }
        if (!recordable.isRecording()) {
            try {
                eventBroadcaster.fireEvent(new RecordingEvent(RecordingEvent.RECORDING_STATE.START));
                recordable.startRecording(destinationFolder);
            } catch (DeviceAccessException e) {
                log.error("can't start recording", e);
            }
        }
    }

    public void stopRecording() {
        if (recordable != null) {
            try {
                Path record = recordable.stopRecording();
                eventBroadcaster.fireEvent(new RecordingEvent(RecordingEvent.RECORDING_STATE.STOP, record.toString()));
            } catch (DeviceAccessException e) {
                log.error("can't stop recording", e);
            }
        }
    }

}
