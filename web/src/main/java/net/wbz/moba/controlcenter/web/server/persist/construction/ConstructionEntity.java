package net.wbz.moba.controlcenter.web.server.persist.construction;


import com.googlecode.jmapper.annotations.JMap;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackPartEntity;

import javax.persistence.*;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Entity
public class ConstructionEntity extends AbstractEntity {

    @JMap
    private String name;

    @OneToMany
    private List<TrackPartEntity> trackPartEntities;

    public ConstructionEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
