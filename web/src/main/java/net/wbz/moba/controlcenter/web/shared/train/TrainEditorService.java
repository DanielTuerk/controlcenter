package net.wbz.moba.controlcenter.web.shared.train;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath("trainEditor")
public interface TrainEditorService extends RemoteService {

    List<Train> getTrains();

    Train getTrain(int address);

    void createTrain(String name);

    void deleteTrain(long trainId);

    void updateTrain(Train train);

}
