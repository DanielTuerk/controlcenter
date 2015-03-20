package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
@RemoteServiceRelativePath("trainEditor")
public interface TrainEditorService extends RemoteService {

    public List<Train> getTrains();

    public Train getTrain(int address);

    public void createTrain(String name);

    public void deleteTrain(long trainId);

    public void updateTrain(Train train);

}
