package net.wbz.moba.controlcenter.web.server.persist.construction;


import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;

import javax.persistence.*;
import java.util.List;

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
