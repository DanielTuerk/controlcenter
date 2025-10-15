package net.wbz.moba.controlcenter.shared.editor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;

/**
 * TODO
 * @author Daniel Tuerk
 */
public class ValidationException extends Exception implements Serializable {

    private Map<AbstractTrackPart, String> errors = new HashMap<>();

    public ValidationException() {
        this("validation failed");
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException addError(AbstractTrackPart abstractTrackPart, String message) {
        errors.put(abstractTrackPart, message);
        return this;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public Map<AbstractTrackPart, String> getErrors() {
        return errors;
    }
}
