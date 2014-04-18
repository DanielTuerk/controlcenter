package net.wbz.moba.controlcenter.web.shared.editor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@RemoteServiceRelativePath("trackeditor")
public interface TrackEditorService extends RemoteService{


    public TrackPart[] loadTrack();
    public void saveTrack(TrackPart[] trackParts);
}
