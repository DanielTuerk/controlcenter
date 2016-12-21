package net.wbz.moba.controlcenter.web.shared.editor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartProxy;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("trackeditor")
public interface TrackEditorService extends RemoteService {

    List<TrackPartProxy> loadTrack();

    void saveTrack(List<TrackPartProxy> trackParts);

    void registerConsumersByConnectedDeviceForTrackParts(List<TrackPartProxy> trackParts);
}
