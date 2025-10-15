package net.wbz.moba.controlcenter.web.server.persist.scenario;

import com.googlecode.jmapper.JMapper;
import net.wbz.moba.controlcenter.web.server.persist.scenario.RouteSequenceEntity;
import net.wbz.moba.controlcenter.web.server.web.DataMapper;
import net.wbz.moba.controlcenter.shared.scenario.RouteSequence;

/**
 * @author Daniel Tuerk
 */
public class RouteSequenceDataMapper extends DataMapper<RouteSequence, RouteSequenceEntity> {

    public RouteSequenceDataMapper() {
        super(RouteSequence.class, RouteSequenceEntity.class);

    }

}
