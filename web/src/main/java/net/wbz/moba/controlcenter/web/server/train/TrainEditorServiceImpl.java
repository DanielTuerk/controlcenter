package net.wbz.moba.controlcenter.web.server.train;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import net.wbz.moba.controlcenter.web.shared.train.TrainFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by Daniel on 08.03.14.
 */
@Singleton
public class TrainEditorServiceImpl extends RemoteServiceServlet implements TrainEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainEditorServiceImpl.class);


    private final TrainManager trainManager;

    @Inject
    public TrainEditorServiceImpl(TrainManager trainManager) {
        this.trainManager = trainManager;
    }

    @Override
    public List<Train> getTrains() {
        return trainManager.getTrains();
    }

    @Override
    public void createTrain(String name) {
        Train train = new Train(name);
        train.setId(System.nanoTime());

        List<TrainFunction> trainFunctions = Lists.newArrayList();
        for(TrainFunction.FUNCTION function : TrainFunction.FUNCTION.values()) {
            trainFunctions.add(new TrainFunction(function, false));
        }
        train.setFunctions(trainFunctions);
        try {
            trainManager.storeTrain(train);
        } catch (Exception e) {
            String msg = String.format("can't create train '%s'", name);
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }


    @Override
    public void deleteTrain(long trainId) {
        throw new NotImplementedException();
    }

    @Override
    public void updateTrain(Train train) {
        try {
            trainManager.storeTrain(train);
        } catch (Exception e) {
            String msg = String.format("can't update train '%s'", train.getName());
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
