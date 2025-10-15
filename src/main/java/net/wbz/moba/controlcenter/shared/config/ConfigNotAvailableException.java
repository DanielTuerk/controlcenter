package net.wbz.moba.controlcenter.shared.config;

/**
 * TODO
 * @author Daniel Tuerk
 */
public class ConfigNotAvailableException extends Exception {
    public ConfigNotAvailableException(String message) {
        super(message);
    }

    public ConfigNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigNotAvailableException() {
    }
}
