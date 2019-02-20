package net.wbz.moba.controlcenter.web.server.web.constrution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import net.wbz.moba.controlcenter.web.server.event.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.bus.RecordingEvent;
import net.wbz.selectrix4java.data.recording.IsRecordable;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class DeviceRecorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRecorder.class);
    private static final String FOLDER = "/record/";

    private IsRecordable recordable;
    private EventBroadcaster eventBroadcaster;
    private final Path destinationFolder;

    @Inject
    public DeviceRecorder(@Named("homePath") String homeDir, EventBroadcaster eventBroadcaster) {
        this.eventBroadcaster = eventBroadcaster;
        this.destinationFolder = Paths.get(homeDir + FOLDER);
        if(!Files.exists(destinationFolder)) {
            try {
                Files.createDirectories(destinationFolder);
            } catch (IOException e) {
                LOGGER.error("can't create recorder output folder: " + destinationFolder);
            }
        }
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
