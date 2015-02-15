package net.wbz.moba.controlcenter.web.server.constrution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.shared.bus.RecordingEvent;
import net.wbz.selectrix4java.data.recording.IsRecordable;
import net.wbz.selectrix4java.device.Device;
import net.wbz.selectrix4java.device.DeviceAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class DeviceRecorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRecorder.class);

    private final String homeDir;

    private IsRecordable recordable;
    private EventBroadcaster eventBroadcaster;

    @Inject
    public DeviceRecorder(@Named("homePath") String homeDir, EventBroadcaster eventBroadcaster) {
        this.homeDir = homeDir;
        this.eventBroadcaster = eventBroadcaster;
    }

    public void startRecording(Device device, String fileName) {
        if (device != null && device instanceof IsRecordable) {
            this.recordable = (IsRecordable) device;
        } else {
            throw new RuntimeException("device is no instance of " + IsRecordable.class.getName());
        }
        if (!recordable.isRecording()) {

            Path destinationFolder = Paths.get(homeDir + "/record/");
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
}
