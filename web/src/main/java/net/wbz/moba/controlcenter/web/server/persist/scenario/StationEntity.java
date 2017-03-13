package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.scenario.StationRail;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "STATION")
public class StationEntity extends AbstractEntity {

    @JMap
    @Column
    private String name;

    @JMap
    @OneToMany(mappedBy = "station")
    private List<StationRailEntity> rails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StationRailEntity> getRails() {
        return rails;
    }

    public void setRails(List<StationRailEntity> rails) {
        this.rails = rails;
    }
}
