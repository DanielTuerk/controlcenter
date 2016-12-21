package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.shared.Identity;


import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by zui on 16.04.2015.
 */
public class TrackPartFunctions  implements Identity {

    @OneToMany(mappedBy = "trackPartFunction", fetch = FetchType.EAGER)
    private List<TrackPartFunctionEntity> functions;

    @ManyToOne(fetch = FetchType.LAZY)
    private TrackPartEntity trackPart;

    public List<TrackPartFunctionEntity> getFunctions() {
        return functions;
    }

    public void setFunctions(List<TrackPartFunctionEntity> functions) {
        this.functions = functions;
    }

    public TrackPartEntity getTrackPart() {
        return trackPart;
    }

    public void setTrackPart(TrackPartEntity trackPart) {
        this.trackPart = trackPart;
    }

    @Override
    public Integer getVersion() {
        return 0;
    }

    @Override
    public Long getId() {
        return null;
    }
}
