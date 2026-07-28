package io.github.danieltuerk.controlcenter.persist.entity;

import io.github.danieltuerk.controlcenter.persist.entity.track.BlockStraightEntity;
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
    @JoinTable(
        name = "STATION_PLATFORM_BLOCK_STRAIGHT",
        joinColumns = @JoinColumn(name = "station_platform_id"),
        inverseJoinColumns = @JoinColumn(name = "block_straight_id")
    )
    public List<BlockStraightEntity> blockStraights;

    @ManyToOne
    public StationEntity station;

}
