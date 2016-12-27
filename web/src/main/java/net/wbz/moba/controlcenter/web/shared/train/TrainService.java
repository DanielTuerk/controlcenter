package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainEntity;
import net.wbz.moba.controlcenter.web.server.web.train.TrainManager;

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
     * @param id    id of the {@link TrainEntity}
     * @param level level of speed
     */
    void updateDrivingLevel(long id, int level);

    /**
     * Change the driving direction of the {@link TrainEntity}.
     *
     * @param id      id of the {@link TrainEntity}
     * @param forward {@code true} for forward and {@code false} for backward
     */
    void toggleDrivingDirection(long id, boolean forward);

    /**
     * Control the state of the functions.
     *
     * @param id       id of the {@link TrainEntity}
     * @param function {@link TrainFunction.FUNCTION}
     * @param state    {@link java.lang.Boolean}
     */
    void setFunctionState(long id, TrainFunction.FUNCTION function, boolean state);

}
