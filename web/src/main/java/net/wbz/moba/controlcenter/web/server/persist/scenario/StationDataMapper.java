package net.wbz.moba.controlcenter.web.server.persist.scenario;

import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.station.Station;

/**
 * @author Daniel Tuerk
 */
public class StationDataMapper extends DataMapper<Station, StationEntity> {

    public StationDataMapper() {
        super(Station.class, StationEntity.class);
    }
}
