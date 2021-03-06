package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;

/**
 * Service to control the trains from the {@link TrainManager}.
 *
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_TRAIN)
public interface TrainService extends RemoteService {

    /**
     * Change level of driving for the train id.
     *
     * @param id id of the {@link Train}
     * @param level level of speed
     */
    void updateDrivingLevel(long id, int level);

    /**
     * Update driving level for given {@link Train}.
     *
     * @param train {@link Train}
     * @param level level of speed
     */
    void updateDrivingLevel(Train train, int level);

    /**
     * Change the driving direction of the {@link TrainEntity}.
     *
     * @param id id of the {@link TrainEntity}
     * @param forward {@code true} for forward and {@code false} for backward
     */
    void toggleDrivingDirection(long id, boolean forward);

    /**
     * Change the driving direction of the {@link TrainEntity}.
     *
     * @param id id of the {@link TrainEntity}
     * @param on {@code true} to activate light
     */
    void toggleHorn(long id, boolean on);

    /**
     * Change the driving direction of the {@link TrainEntity}.
     *
     * @param id id of the {@link TrainEntity}
     * @param on {@code true} to activate horn
     */
    void toggleLight(long id, boolean on);

    /**
     * Control the state of the functions.
     *
     * @param id id of the {@link TrainEntity}
     * @param function {@link TrainFunction}
     * @param state {@link java.lang.Boolean}
     */
    void toggleFunctionState(long id, TrainFunction function, boolean state);

}
