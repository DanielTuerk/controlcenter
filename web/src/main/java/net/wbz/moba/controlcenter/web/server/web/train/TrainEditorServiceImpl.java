package net.wbz.moba.controlcenter.web.server.web.train;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.wbz.moba.controlcenter.web.server.EventBroadcaster;
import net.wbz.moba.controlcenter.web.server.persist.train.TrainDao;
import net.wbz.moba.controlcenter.web.shared.train.Train;
import net.wbz.moba.controlcenter.web.shared.train.TrainDataChangedEvent;
import net.wbz.moba.controlcenter.web.shared.train.TrainEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Daniel Tuerk
 */
@Singleton
public class TrainEditorServiceImpl extends RemoteServiceServlet implements TrainEditorService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainEditorServiceImpl.class);

    private final TrainManager trainManager;
    private final EventBroadcaster eventBroadcaster;
    private final TrainDao trainDao;

    @Inject
    public TrainEditorServiceImpl(TrainManager trainManager,
                                  final EventBroadcaster eventBroadcaster, TrainDao trainDao) {
        this.trainManager = trainManager;
        this.eventBroadcaster = eventBroadcaster;
        this.trainDao = trainDao;
    }

    public List<Train> getTrains() {
        return trainManager.getTrains();
    }

    public Train getTrain(int address) {
        return trainManager.getTrain(address);
    }

    public void createTrain(Train train) {
//        Train train = new Train(name);
//
//        Set<TrainFunctionEntity> trainFunctions = new HashSet<>();
//        for (TrainFunctionEntity.FUNCTION function : TrainFunctionEntity.FUNCTION.values()) {
//            trainFunctions.add(new TrainFunctionEntity(function, false));
//        }
//        train.setFunctions(trainFunctions);
        try {
            trainManager.createTrain(train);
        } catch (Exception e) {
            String msg = String.format("can't create train '%s'", train.getName());
            LOG.error(msg, e);
        }
    }

    public void deleteTrain(long trainId) {
//        try {
        trainManager.deleteTrain(trainId);

        eventBroadcaster.fireEvent(new TrainDataChangedEvent(trainId));
//        } catch (TrainException e) {
//            String msg = String.format("can't delete train '%s'", trainId);
//            LOG.error(msg, e);
//            throw new RuntimeException(msg, e);
//        }
    }

    public void updateTrain(Train train) {
        try {
            trainManager.updateTrain(train);

            eventBroadcaster.fireEvent(new TrainDataChangedEvent(train.getId()));
        } catch (Exception e) {
            String msg = String.format("can't update train '%s'", train.getName());
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
