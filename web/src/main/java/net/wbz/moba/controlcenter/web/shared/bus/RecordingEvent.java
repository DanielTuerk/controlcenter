package net.wbz.moba.controlcenter.web.shared.bus;

import de.novanic.eventservice.client.event.Event;

/**
 * @author Daniel Tuerk
 */
public class RecordingEvent implements Event {

    public enum STATE {START, STOP}

    private STATE state;
    private String filePath;

    public RecordingEvent() {
    }

    public RecordingEvent(STATE state) {
        this.state = state;
    }

    public RecordingEvent(STATE state, String filePath) {
        this.state = state;
        this.filePath = filePath;
    }

    public STATE getState() {
        return state;
    }

    public String getFilePath() {
        return filePath;
    }
}
