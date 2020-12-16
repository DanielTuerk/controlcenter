package net.wbz.moba.controlcenter.web.server.persist.scenario;

import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.web.shared.station.StationPlatform;

/**
 * @author Daniel Tuerk
 */
public class StationPlatformDataMapper extends DataMapper<StationPlatform, StationPlatformEntity> {

    public StationPlatformDataMapper() {
        super(StationPlatform.class, StationPlatformEntity.class);
    }
}
