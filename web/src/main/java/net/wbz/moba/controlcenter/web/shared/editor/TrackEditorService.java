package net.wbz.moba.controlcenter.web.shared.editor;

import net.wbz.moba.controlcenter.web.shared.track.model.TrackPart;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */

public interface TrackEditorService {


    public TrackPart[] loadTrack();

    public void saveTrack(TrackPart[] trackParts);

    public void registerConsumersByConnectedDeviceForTrackParts(TrackPart[] trackParts);
}
