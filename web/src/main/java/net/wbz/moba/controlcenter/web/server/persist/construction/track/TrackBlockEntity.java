package net.wbz.moba.controlcenter.web.server.persist.construction.track;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.googlecode.jmapper.annotations.JMap;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.ConstructionEntity;
import net.wbz.moba.controlcenter.web.shared.constrution.Construction;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "TRACK_BLOCK")
public class TrackBlockEntity extends AbstractEntity {

    @JMap
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BusDataConfigurationEntity blockFunction;

    @JMap
    @ManyToOne(fetch = FetchType.LAZY)
    private ConstructionEntity construction;

    @JMap
    @Column
    private String name;

    public TrackBlockEntity() {
    }

    public BusDataConfigurationEntity getBlockFunction() {
        return blockFunction;
    }

    public void setBlockFunction(BusDataConfigurationEntity blockFunction) {
        this.blockFunction = blockFunction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConstructionEntity getConstruction() {
        return construction;
    }

    public void setConstruction(ConstructionEntity construction) {
        this.construction = construction;
    }
}
