package net.wbz.moba.controlcenter.web.shared.station;

import net.wbz.moba.controlcenter.web.shared.Event;

/**
 * Event if the data of a {@link Station} has changed.
 *
 * @author Daniel Tuerk
 */
public class StationDataChangedEvent implements Event {

    public StationDataChangedEvent() {
    }

    @Override
    public String toString() {
        return "StationDataChangedEvent{}";
    }
}
