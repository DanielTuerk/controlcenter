package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.inject.Singleton;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import net.wbz.moba.controlcenter.shared.editor.ValidationException;
import net.wbz.moba.controlcenter.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.shared.track.model.TrackBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorService.class);

    private final TrackManager trackManager;
    private Set<TrackPartValidator> validators;

    @Inject
    public TrackEditorService(TrackManager trackManager, Set<TrackPartValidator> validators) {
        this.trackManager = trackManager;
        this.validators = validators;
    }

    public void saveTrack(Collection<AbstractTrackPart> trackParts) throws ValidationException {
        validate(trackParts);
        trackManager.saveTrack(trackParts);
    }

    private void validate(Collection<AbstractTrackPart> trackParts) throws ValidationException {
        ValidationException ex = null;
        for (TrackPartValidator validator : validators) {
            try {
                validator.validate(trackParts);
            } catch (ValidationException e) {
                ex = e;
            }
        }
        if (ex != null && ex.hasError()) {
            throw ex;
        }
    }

    public Collection<TrackBlock> loadTrackBlocks() {
        return trackManager.loadTrackBlocks();
    }

    public void deleteTrackBlock(TrackBlock trackBlock){
        trackManager.deleteTrackBlock(trackBlock);
    }

    public void saveTrackBlocks(Collection<TrackBlock> trackBlocks) {
        trackManager.saveTrackBlocks(trackBlocks);
    }

    public Collection<AbstractTrackPart> loadTrack() {
        return trackManager.getTrack();
    }

}
