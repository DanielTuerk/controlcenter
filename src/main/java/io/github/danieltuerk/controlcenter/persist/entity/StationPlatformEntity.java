package io.github.danieltuerk.controlcenter.persist.entity;

import io.github.danieltuerk.controlcenter.persist.entity.track.TrackBlockEntity;
import jakarta.persistence.*;

import java.util.List;

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
