package net.wbz.moba.controlcenter.web.shared.track.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Daniel Tuerk
 */
@Entity
public class TrackPartFunction  implements IsSerializable, Serializable {
    @Id
    private String functionKey;

    private Configuration configuration;

    @ManyToOne(fetch= FetchType.LAZY)
    private TrackPart trackPart;

    public TrackPartFunction(TrackPart trackPart, String key, Configuration configuration) {
        this.trackPart = trackPart;
        this.functionKey = key;
        this.configuration = configuration;
    }

    public TrackPartFunction() {
    }

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

    public TrackPart getTrackPart() {
        return trackPart;
    }

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
