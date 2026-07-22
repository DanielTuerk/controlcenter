package io.github.danieltuerk.controlcenter.shared.scenario;

import java.io.Serializable;

/**
 * Exception if there is no {@link Track} for a
 * {@link Route}.
 *
 * @author Daniel Tuerk
 */
public class TrackNotFoundException extends Exception implements Serializable {

    public TrackNotFoundException() {
        this("no track found");
    }

    public TrackNotFoundException(String message) {
        super(message);
    }

}
