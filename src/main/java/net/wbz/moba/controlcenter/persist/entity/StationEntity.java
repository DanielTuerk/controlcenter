package net.wbz.moba.controlcenter.persist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;


/**
 * @author Daniel Tuerk
 */
@Entity
@Table(name = "STATION")
public class StationEntity extends AbstractEntity {

    public String name;

    @OneToMany(mappedBy = "station", fetch = FetchType.EAGER)
    public List<StationPlatformEntity> platforms;

}
