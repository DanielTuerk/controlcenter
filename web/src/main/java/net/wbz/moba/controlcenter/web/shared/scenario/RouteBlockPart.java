package net.wbz.moba.controlcenter.web.shared.scenario;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;
import net.wbz.moba.controlcenter.web.shared.track.model.Switch;

/**
 * @author Daniel Tuerk
 */
public class RouteBlockPart extends AbstractDto {

    /**
     * Switch for the route.
     * TODO refactor to abstract parts which have toggle functions
     */
    @JMap
    private Switch switchTrackPart;

    /**
     * State of the switch to set for the route.
     */
    @JMap
    private boolean state;

    public Switch getSwitchTrackPart() {
        return switchTrackPart;
    }

    public void setSwitchTrackPart(Switch switchTrackPart) {
        this.switchTrackPart = switchTrackPart;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
