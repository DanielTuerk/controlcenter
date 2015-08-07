package net.wbz.moba.controlcenter.web.shared.track.model;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zui on 16.04.2015.
 */
public class TrackPartFunctions implements Serializable {

    @OneToMany(mappedBy = "trackPartFunction", fetch = FetchType.EAGER)
    private List<TrackPartFunction> functions;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private TrackPart trackPart;

    public List<TrackPartFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<TrackPartFunction> functions) {
        this.functions = functions;
    }

//    public TrackPart getTrackPart() {
//        return trackPart;
//    }
//
//    public void setTrackPart(TrackPart trackPart) {
//        this.trackPart = trackPart;
//    }
}
