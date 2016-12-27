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
    private AbstractTrackPartEntity trackPart;

    public List<TrackPartFunctionEntity> getFunctions() {
        return functions;
    }

    public void setFunctions(List<TrackPartFunctionEntity> functions) {
        this.functions = functions;
    }

    public AbstractTrackPartEntity getTrackPart() {
        return trackPart;
    }

    public void setTrackPart(AbstractTrackPartEntity trackPart) {
        this.trackPart = trackPart;
    }


    @Override
    public Long getId() {
        return null;
    }
}
