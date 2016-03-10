package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service to control the trains from the {@link net.wbz.moba.controlcenter.web.server.train.TrainManager}.
 *
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("trainService")
public interface TrainService extends RemoteService {

    /**
     * Change level of driving for the train id.
     *
     * @param id    id of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     * @param level level of speed
     */
    public void updateDrivingLevel(long id, int level);

    /**
     * Change the driving direction of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}.
     *
     * @param id        id of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     * @param direction {@link net.wbz.moba.controlcenter.web.shared.train.Train.DIRECTION}
     */
    public void toggleDrivingDirection(long id, Train.DIRECTION direction);

    /**
     * Control the state of the functions.
     *
     * @param id       id of the {@link net.wbz.moba.controlcenter.web.shared.train.Train}
     * @param function {@link net.wbz.moba.controlcenter.web.shared.train.TrainFunction.FUNCTION}
     * @param state    {@link java.lang.Boolean}
     */
    public void setFunctionState(long id, TrainFunction.FUNCTION function, boolean state);

}
