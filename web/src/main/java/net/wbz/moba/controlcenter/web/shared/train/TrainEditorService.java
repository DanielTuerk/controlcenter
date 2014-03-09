package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import net.wbz.moba.controlcenter.web.shared.scenario.Scenario;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@RemoteServiceRelativePath("trainEditor")
public interface TrainEditorService extends RemoteService {

    /**
     * @gwt.typeArgs <Train>
     */
    public List<Train> getTrains();

    public void createTrain(String name);

    public void deleteTrain(long trainId);

    public void updateTrain(Train train);

}
