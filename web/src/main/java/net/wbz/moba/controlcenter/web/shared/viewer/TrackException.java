package net.wbz.moba.controlcenter.web.shared.viewer;

/**
 * @author Daniel Tuerk
 */
public class TrackException extends Exception {
    public TrackException(String s) {
        super(s);
    }

    public TrackException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
