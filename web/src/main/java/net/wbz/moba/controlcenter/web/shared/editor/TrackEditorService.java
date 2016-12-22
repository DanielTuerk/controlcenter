package net.wbz.moba.controlcenter.web.shared.editor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("trackeditor")
public interface TrackEditorService extends RemoteService {

    List<TrackPart> loadTrack();

    void saveTrack(List<TrackPart> trackParts);

    void registerConsumersByConnectedDeviceForTrackParts(List<TrackPart> trackParts);
}
