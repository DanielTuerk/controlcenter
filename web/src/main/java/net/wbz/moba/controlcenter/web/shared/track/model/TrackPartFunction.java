package net.wbz.moba.controlcenter.web.shared.track.model;

import net.wbz.moba.controlcenter.web.shared.Identity;


import javax.persistence.*;

/**
 * @author Daniel Tuerk
 */
@Entity
public class TrackPartFunction implements Identity {

    @Id
    @GeneratedValue
    @Column(name = "TRACKPARTFUNCTION_ID")
    private long id;

    private String functionKey;

    @OneToOne
    private Configuration configuration;

//    @ManyToMany(mappedBy="functions",fetch = FetchType.LAZY)
//    private List<TrackPart> trackParts = new ArrayList<>();

//    public TrackPartFunction(TrackPart trackPart, String key, Configuration configuration) {
////        this.trackPart = trackPart;
//        this.functionKey = key;
//        this.configuration = configuration;
//    }
    public TrackPartFunction(String key, Configuration configuration) {
//        this.trackPart = trackPart;
        this.functionKey = key;
        this.configuration = configuration;
    }

    public TrackPartFunction() {
    }

    @Override
    public Integer getVersion() {
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public List<TrackPart> getTrackPart() {
//        return trackPart;
//    }
//
//    public void setTrackPart(List<TrackPart> trackPart) {
//        this.trackPart = trackPart;
//    }


//    public List<TrackPart> getTrackParts() {
//        return trackParts;
//    }
//
//    public void setTrackParts(List<TrackPart> trackParts) {
//        this.trackParts = trackParts;
//    }

    public String getFunctionKey() {
        return functionKey;
    }

    public void setFunctionKey(String functionKey) {
        this.functionKey = functionKey;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

//    public TrackPart getTrackPart() {
//        return trackPart;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackPartFunction that = (TrackPartFunction) o;

        if (functionKey != null ? !functionKey.equals(that.functionKey) : that.functionKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return functionKey != null ? functionKey.hashCode() : 0;
    }
}
