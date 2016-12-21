package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.web.bindery.requestfactory.shared.Request;
import net.wbz.moba.controlcenter.web.guice.requestFactory.InjectingServiceLocator;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainFunctionEntity;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;
import net.wbz.moba.controlcenter.web.server.web.train.TrainServiceImpl;

import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

/**
 * Service to control the trains from the {@link TrainManager}.
 *
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("train")
public interface TrainService extends RemoteService {

    /**
     * Change level of driving for the train id.
     *
     * @param id id of the {@link TrainEntity}
     * @param level level of speed
     */
    void updateDrivingLevel(long id, int level);

    /**
     * Change the driving direction of the {@link TrainEntity}.
     *
     * @param id id of the {@link TrainEntity}
     * @param direction {@link TrainEntity.DIRECTION}
     */
    void toggleDrivingDirection(long id, boolean forward);

    /**
     * Control the state of the functions.
     *
     * @param id id of the {@link TrainEntity}
     * @param function {@link TrainFunctionEntity.FUNCTION}
     * @param state {@link java.lang.Boolean}
     */
    void setFunctionState(long id, TrainFunctionEntity.FUNCTION function, boolean state);

}
