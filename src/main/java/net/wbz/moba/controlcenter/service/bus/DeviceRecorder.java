package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.LocalFileService;
import net.wbz.moba.controlcenter.shared.bus.RecordingEvent;
import net.wbz.selectrix4java.data.recording.IsRecordable;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

import java.nio.file.Path;

/**
 * @author Daniel Tuerk
 */
@Slf4j
@ApplicationScoped
public class DeviceRecorder {

    private static final String FOLDER = "/record/";

    private final EventBroadcaster eventBroadcaster;
    private final Path destinationFolder;

    private IsRecordable recordable;

    @Inject
    public DeviceRecorder(LocalFileService localFileService, EventBroadcaster eventBroadcaster) {
        this.eventBroadcaster = eventBroadcaster;
        this.destinationFolder = localFileService.getDir(FOLDER);
    }

    public void startRecording(Device device, String fileName) {
        // TODO file name
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

    public Path getDestinationFolder() {
        return destinationFolder;
    }
}
