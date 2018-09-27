package net.wbz.moba.controlcenter.web.server.persist.scenario;

/**
 * Exception if there is no {@link net.wbz.moba.controlcenter.web.shared.scenario.Track} for a
 * {@link net.wbz.moba.controlcenter.web.shared.scenario.Route}.
 *
 * @author Daniel Tuerk
 */
public class TrackNotFoundException extends Exception {

    TrackNotFoundException() {
        this("no track found!");
    }

    TrackNotFoundException(String message) {
        super(message);
    }

}
