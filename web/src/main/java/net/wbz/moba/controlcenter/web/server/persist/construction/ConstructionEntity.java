package net.wbz.moba.controlcenter.web.server.persist.construction;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "CONSTRUCTION")
public class ConstructionEntity extends AbstractEntity {

    @JMap
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    private List<AbstractTrackPartEntity> trackPartEntities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbstractTrackPartEntity> getTrackPartEntities() {
        return trackPartEntities;
    }

    public void setTrackPartEntities(List<AbstractTrackPartEntity> trackPartEntities) {
        this.trackPartEntities = trackPartEntities;
    }
}
