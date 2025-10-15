package net.wbz.moba.controlcenter.web.server.persist.construction;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;




import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "CONSTRUCTION")
public class ConstructionEntity extends AbstractEntity {

    private String name;

    @OneToMany(mappedBy = "construction", fetch = FetchType.LAZY)
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
