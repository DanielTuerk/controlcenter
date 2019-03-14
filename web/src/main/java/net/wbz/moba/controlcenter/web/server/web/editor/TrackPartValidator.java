package net.wbz.moba.controlcenter.web.server.web.editor;

import java.util.Collection;
import net.wbz.moba.controlcenter.web.shared.editor.ValidationException;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;

/**
 * Validator to check {@link AbstractTrackPart}s.
 *
 * @author Daniel Tuerk
 */
public interface TrackPartValidator {

    /**
     * Validate the given {@link AbstractTrackPart}s.
     *
     * @param trackParts element to validate
     * @throws ValidationException invalid entries
     */
    void validate(Collection<AbstractTrackPart> trackParts) throws ValidationException;
}
