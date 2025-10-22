package net.wbz.moba.controlcenter.shared.viewer;

import java.util.Objects;
import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Created by Daniel on 18.04.2014.
 */
@Schema(description = "track status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrackPartBlockEvent implements StateEvent {

    private BusDataConfiguration config;
    private STATE state;
    public TrackPartBlockEvent() {
    }

    public TrackPartBlockEvent(BusDataConfiguration config, STATE state) {
        this.config = config;
        this.state = state;
    }

    public BusDataConfiguration getConfig() {
        return config;
    }

    public void setConfig(BusDataConfiguration config) {
        this.config = config;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    @Override
    public String getCacheKey() {
        return getClass().getName() + ":" + config.getIdentifierKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrackPartBlockEvent that = (TrackPartBlockEvent) o;
        return Objects.equals(config, that.config) && state == that.state;
    }

    @Override
    public int hashCode() {

        return Objects.hash(config, state);
    }

    @Override
    public String toString() {
        return "TrackPartBlockEvent{" + "config=" + config + ", state=" + state + '}';
    }

    public enum STATE {
        UNKNOWN, FREE, USED
    }
}
