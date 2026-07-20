package io.github.danieltuerk.controlcenter.shared.config;

/**
 * @author Daniel Tuerk
 */
public class ConfigNotAvailableException extends RuntimeException {
    public ConfigNotAvailableException(String message) {
        super(message);
    }
}
