package net.wbz.moba.controlcenter.service.train;

/**
 * Exception for a {@link net.wbz.moba.controlcenter.web.shared.train.Train} data manipulation.
 * 
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
