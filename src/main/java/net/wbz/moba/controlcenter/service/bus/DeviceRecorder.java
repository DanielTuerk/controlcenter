package net.wbz.moba.controlcenter.service.bus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.file.Path;
import net.wbz.moba.controlcenter.EventBroadcaster;
import net.wbz.moba.controlcenter.service.LocalFileService;
import net.wbz.moba.controlcenter.shared.bus.RecordingEvent;
import net.wbz.selectrix4java.data.recording.IsRecordable;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.jboss.logging.Logger;

/**
 * @author Daniel Tuerk
 */
@ApplicationScoped
public class DeviceRecorder {

    private static final Logger LOGGER = Logger.getLogger(DeviceRecorder.class);
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
                eventBroadcaster.fireEvent(new RecordingEvent(RecordingEvent.STATE.START));
                recordable.startRecording(destinationFolder);
            } catch (DeviceAccessException e) {
                LOGGER.error("can't start recording", e);
            }
        }
    }

    public void stopRecording() {
        if (recordable != null) {
            try {
                Path record = recordable.stopRecording();
                eventBroadcaster.fireEvent(new RecordingEvent(RecordingEvent.STATE.STOP, record.toString()));
            } catch (DeviceAccessException e) {
                LOGGER.error("can't stop recording", e);
            }
        }
    }

    public Path getDestinationFolder() {
        return destinationFolder;
    }
}
