package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@RemoteServiceRelativePath("trainService")
public interface TrainService extends RemoteService {

    public void updateDrivingLevel(long id, int level);
    public void toggleDrivingDirection(long id, Train.DIRECTION direction);
    public void setFunctionState(long id, TrainFunction.FUNCTION function, boolean state);

}
