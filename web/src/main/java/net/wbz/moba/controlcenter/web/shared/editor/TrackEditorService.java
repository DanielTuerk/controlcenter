package net.wbz.moba.controlcenter.web.shared.editor;

import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackBlock;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_TRACK_EDITOR)
public interface TrackEditorService extends RemoteService {

    Collection<AbstractTrackPart> loadTrack();

    void saveTrack(Collection<AbstractTrackPart> trackParts) throws ValidationException;

    Collection<TrackBlock> loadTrackBlocks();

    void deleteTrackBlock(TrackBlock trackBlock);

    void saveTrackBlocks(Collection<TrackBlock> trackBlocks);
}
