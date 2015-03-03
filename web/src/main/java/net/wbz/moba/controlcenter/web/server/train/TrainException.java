package net.wbz.moba.controlcenter.web.server.train;

/**
 * @author Daniel Tuerk
 */
public class TrainException extends Exception {
    public TrainException(String message) {
        super(message);
    }

    public TrainException(String message, Throwable cause) {
        super(message, cause);
    }
}
