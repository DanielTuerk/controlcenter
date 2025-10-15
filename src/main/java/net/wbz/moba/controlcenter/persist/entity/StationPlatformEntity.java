package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import net.wbz.moba.controlcenter.persist.entity.track.TrackBlockEntity;

/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "STATION_PLATFORM")
public class StationPlatformEntity extends AbstractEntity {

    public String name;

    @OneToMany(fetch = FetchType.EAGER)
    public List<TrackBlockEntity> trackBlocks;

    @ManyToOne
    public StationEntity station;

}
