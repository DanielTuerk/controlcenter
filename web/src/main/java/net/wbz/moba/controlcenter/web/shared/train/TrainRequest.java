package net.wbz.moba.controlcenter.web.shared.train;

import com.google.web.bindery.requestfactory.shared.Request;
import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.train.TrainService;

import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

/**
 * Service to control the trains from the {@link net.wbz.moba.controlcenter.web.server.train.TrainManager}.
 *
 * @author Daniel Tuerk
 */
@Service(value = TrainService.class, locator = InjectingServiceLocator.class)
public interface TrainRequest extends RequestContext {

    /**
     * Change level of driving for the train id.
     *
     * @param id id of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     * @param level level of speed
     */
    Request<Void> updateDrivingLevel(long id, int level);

    /**
     * Change the driving direction of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}.
     *
     * @param id id of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     * @param direction {@link net.wbz.moba.controlcenter.web.shared.train.Train.DIRECTION}
     */
    Request<Void> toggleDrivingDirection(long id, Train.DIRECTION direction);

    /**
     * Control the state of the functions.
     *
     * @param id id of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     * @param function {@link net.wbz.moba.controlcenter.web.shared.train.TrainFunction.FUNCTION}
     * @param state {@link java.lang.Boolean}
     */
    Request<Void> setFunctionState(long id, TrainFunction.FUNCTION function, boolean state);

}
