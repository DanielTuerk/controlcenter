package net.wbz.moba.controlcenter.web.server.persist.scenario;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.server.persist.construction.track.TrackBlockEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.BusDataConfiguration;

/**
 * @author Daniel Tuerk
 */
@Entity(name = "STATION")
public class StationEntity extends AbstractEntity {

    private String name;

    @OneToMany
    private List<StationRailEntity> rails;

}
