package net.wbz.moba.controlcenter.web.server.web.editor;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.editor.ValidationException;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackEditorServiceImpl extends RemoteServiceServlet implements TrackEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final TrackManager trackManager;
    private Set<TrackPartValidator> validators;

    @Inject
    public TrackEditorServiceImpl(TrackManager trackManager, Set<TrackPartValidator> validators) {
        this.trackManager = trackManager;
        this.validators = validators;
    }

    @Override
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

    @Override
    public Collection<TrackBlock> loadTrackBlocks() {
        return trackManager.loadTrackBlocks();
    }

    @Override
    public void deleteTrackBlock(TrackBlock trackBlock){
        trackManager.deleteTrackBlock(trackBlock);
    }

    @Override
    public void saveTrackBlocks(Collection<TrackBlock> trackBlocks) {
        trackManager.saveTrackBlocks(trackBlocks);
    }

    @Override
    public Collection<AbstractTrackPart> loadTrack() {
        return trackManager.getTrack();
    }

}
