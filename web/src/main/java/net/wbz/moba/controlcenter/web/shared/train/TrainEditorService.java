package net.wbz.moba.controlcenter.web.shared.train;

import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.wbz.moba.controlcenter.web.guice.MyGuiceServletConfig;

/**
 * @author Daniel Tuerk
 */
@RemoteServiceRelativePath(MyGuiceServletConfig.SERVICE_TRAIN_EDITOR)
public interface TrainEditorService extends RemoteService {

    Collection<Train> getTrains();

    Train getTrain(int address);

    void createTrain(Train train);

    void deleteTrain(long trainId);

    void updateTrain(Train train);

}
