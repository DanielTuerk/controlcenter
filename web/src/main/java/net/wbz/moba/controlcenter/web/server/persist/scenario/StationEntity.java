package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.googlecode.jmapper.annotations.JMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "STATION")
public class StationEntity extends AbstractEntity {

    @JMap
    @Column
    private String name;

    @JMap
    @OneToMany(mappedBy = "station", fetch = FetchType.EAGER)
    private List<StationPlatformEntity> platforms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StationPlatformEntity> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<StationPlatformEntity> rails) {
        this.platforms = rails;
    }
}
