package net.wbz.moba.controlcenter.web.shared.scenario;

import java.io.Serializable;

/**
 * Exception if there is no {@link net.wbz.moba.controlcenter.web.shared.scenario.Track} for a
 * {@link net.wbz.moba.controlcenter.web.shared.scenario.Route}.
 *
 * @author Daniel Tuerk
 */
public class TrackNotFoundException extends Exception implements Serializable {

    public TrackNotFoundException() {
        this("no track found!");
    }

    public TrackNotFoundException(String message) {
        super(message);
    }

}
