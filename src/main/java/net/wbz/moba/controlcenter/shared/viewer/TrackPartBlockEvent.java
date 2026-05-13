package net.wbz.moba.controlcenter.shared.viewer;

import net.wbz.moba.controlcenter.shared.StateEvent;
import net.wbz.moba.controlcenter.shared.track.model.BusDataConfiguration;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Objects;

@Schema(description = "track status update sent via WebSocket")
@Tag(ref = "websocket")
public class TrackPartBlockEvent implements StateEvent {

    private BusDataConfiguration config;
    private BLOCK_STATE state;

    public TrackPartBlockEvent(BusDataConfiguration config, BLOCK_STATE state) {
        this.config = config;
        this.state = state;
    }

    public BusDataConfiguration getConfig() {
        return config;
    }

    public void setConfig(BusDataConfiguration config) {
        this.config = config;
    }

    public BLOCK_STATE getState() {
        return state;
    }

    public void setState(BLOCK_STATE state) {
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

    public enum BLOCK_STATE {
        UNKNOWN, FREE, USED
    }
}
