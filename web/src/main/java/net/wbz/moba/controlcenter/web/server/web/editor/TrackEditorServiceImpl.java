package net.wbz.moba.controlcenter.web.server.web.editor;

import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.shared.editor.TrackEditorService;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrackEditorServiceImpl extends RemoteServiceServlet implements TrackEditorService {

    private static final Logger log = LoggerFactory.getLogger(TrackEditorServiceImpl.class);

    private final TrackManager trackManager;

    @Inject
    public TrackEditorServiceImpl(TrackManager trackManager) {
        this.trackManager = trackManager;
    }

    @Override
    public void saveTrack(Collection<AbstractTrackPart> trackParts) {
        trackManager.saveTrack(trackParts);
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
