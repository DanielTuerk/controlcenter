package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

import javax.persistence.*;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "TRACKPART_FUNCTION")
public class TrackPartFunctionEntity extends AbstractEntity {

    @JMap
    @Column
    private String functionKey;

    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity configuration;

//    @ManyToMany(mappedBy="functions",fetch = FetchType.LAZY)
//    private List<AbstractTrackPartEntity> trackParts = new ArrayList<>();

    //    public TrackPartFunctionEntity(AbstractTrackPartEntity trackPart, String key, BusDataConfigurationEntity configuration) {
////        this.trackPart = trackPart;
//        this.functionKey = key;
//        this.configuration = configuration;
//    }
    public TrackPartFunctionEntity(String key, BusDataConfigurationEntity configuration) {
//        this.trackPart = trackPart;
        this.functionKey = key;
        this.configuration = configuration;
    }

    public TrackPartFunctionEntity() {
    }

//    public List<AbstractTrackPartEntity> getTrackPart() {
//        return trackPart;
//    }
//
//    public void setTrackPart(List<AbstractTrackPartEntity> trackPart) {
//        this.trackPart = trackPart;
//    }


//    public List<AbstractTrackPartEntity> getTrackParts() {
//        return trackParts;
//    }
//
//    public void setTrackParts(List<AbstractTrackPartEntity> trackParts) {
//        this.trackParts = trackParts;
//    }

    public String getFunctionKey() {
        return functionKey;
    }

    public void setFunctionKey(String functionKey) {
        this.functionKey = functionKey;
    }

    public BusDataConfigurationEntity getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BusDataConfigurationEntity configuration) {
        this.configuration = configuration;
    }

//    public AbstractTrackPartEntity getTrackPart() {
//        return trackPart;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackPartFunctionEntity that = (TrackPartFunctionEntity) o;

        if (functionKey != null ? !functionKey.equals(that.functionKey) : that.functionKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return functionKey != null ? functionKey.hashCode() : 0;
    }

}
