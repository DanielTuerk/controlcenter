package net.wbz.moba.controlcenter.web.server.persist.scenario;

import net.wbz.moba.controlcenter.web.server.persist.scenario.ScenarioEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.shared.scenario.Scenario;

/**
 * @author Daniel Tuerk
 */
public class ScenarioDataMapper extends DataMapper<Scenario, ScenarioEntity> {

    public ScenarioDataMapper() {
        super(Scenario.class, ScenarioEntity.class);
    }


}
