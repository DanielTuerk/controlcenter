package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

import javax.persistence.*;

/**
 * @author Daniel Tuerk
 */
@Entity
public class TrackPartFunctionEntity extends AbstractEntity {


    private String functionKey;

    @OneToOne
    private TrackPartConfigurationEntity configuration;

//    @ManyToMany(mappedBy="functions",fetch = FetchType.LAZY)
//    private List<TrackPartEntity> trackParts = new ArrayList<>();

    //    public TrackPartFunctionEntity(TrackPartEntity trackPart, String key, TrackPartConfigurationEntity configuration) {
////        this.trackPart = trackPart;
//        this.functionKey = key;
//        this.configuration = configuration;
//    }
    public TrackPartFunctionEntity(String key, TrackPartConfigurationEntity configuration) {
//        this.trackPart = trackPart;
        this.functionKey = key;
        this.configuration = configuration;
    }

    public TrackPartFunctionEntity() {
    }

//    public List<TrackPartEntity> getTrackPart() {
//        return trackPart;
//    }
//
//    public void setTrackPart(List<TrackPartEntity> trackPart) {
//        this.trackPart = trackPart;
//    }


//    public List<TrackPartEntity> getTrackParts() {
//        return trackParts;
//    }
//
//    public void setTrackParts(List<TrackPartEntity> trackParts) {
//        this.trackParts = trackParts;
//    }

    public String getFunctionKey() {
        return functionKey;
    }

    public void setFunctionKey(String functionKey) {
        this.functionKey = functionKey;
    }

    public TrackPartConfigurationEntity getConfiguration() {
        return configuration;
    }

    public void setConfiguration(TrackPartConfigurationEntity configuration) {
        this.configuration = configuration;
    }

//    public TrackPartEntity getTrackPart() {
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
