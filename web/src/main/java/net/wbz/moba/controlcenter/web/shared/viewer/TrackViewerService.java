package net.wbz.moba.controlcenter.web.shared.viewer;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.shared.track.model.Configuration;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@RemoteServiceRelativePath("trackviewer")
public interface TrackViewerService extends RemoteService {

    public void toggleTrackPart(Configuration configuration, boolean state);
    public boolean getTrackPartState(Configuration configuration);
}
